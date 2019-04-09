package net.minecraft.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spigotmc.SpigotConfig;

import com.destroystokyo.paper.PaperConfig;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.ProfileLookupCallback;
import io.akarin.server.core.AkarinAsyncExecutor;
import io.akarin.server.core.AkarinGlobalConfig;
import net.minecraft.server.UserCache.UserCacheEntry;

public class AkarinUserCache {
    private final static Logger LOGGER = LogManager.getLogger("Akarin");
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    
    // To reduce date creation
    private final static long RECREATE_DATE_INTERVAL = TimeUnit.MILLISECONDS.convert(10, TimeUnit.MINUTES);
    private static long lastWarpExpireDate;
    private static Date lastExpireDate;
    
    /**
     * All user caches
     * String username -> UserCacheEntry cacheEntry (profile and expire date)
     */
    private final Cache<String, UserCacheEntry> profiles = Caffeine.newBuilder().maximumSize(SpigotConfig.userCacheCap).build();
    
    private final GameProfileRepository profileHandler;
    protected final Gson gson;
    private final File userCacheFile;
    
    protected static boolean isOnlineMode() {
        return UserCache.isOnlineMode() || (SpigotConfig.bungee && PaperConfig.bungeeOnlineMode);
    }

    private static Date createExpireDate(boolean force) {
        long now = System.currentTimeMillis();
        if (force || (now - lastWarpExpireDate) > RECREATE_DATE_INTERVAL) {
            lastWarpExpireDate = now;
            Calendar calendar = Calendar.getInstance();
            
            calendar.add(Calendar.SECOND, AkarinGlobalConfig.userCacheExpireDays);
            return lastExpireDate = calendar.getTime();
        }

        return lastExpireDate;
    }

    private static boolean isExpired(UserCacheEntry entry) {
        return System.currentTimeMillis() >= entry.getExpireDate().getTime();
    }
 
    private static UserCacheEntry refreshExpireDate(UserCacheEntry entry) {
        return new UserCacheEntry(entry.getProfile(), createExpireDate(true));
    }

    private static GameProfile lookup(GameProfileRepository profileRepo, String username, @Nonnull Consumer<GameProfile> cachedCallbackHandler, boolean async) {
        GameProfile[] gameProfile = new GameProfile[1];
        ProfileLookupCallback handler = new ProfileLookupCallback() {
            @Override
            public void onProfileLookupSucceeded(GameProfile gameprofile) {
                cachedCallbackHandler.accept(gameprofile);
                gameProfile[0] = gameprofile;
            }

            @Override
            public void onProfileLookupFailed(GameProfile gameprofile, Exception ex) {
                LOGGER.warn("Failed to lookup profile for player {}, applying offline UUID.", gameprofile.getName());
                GameProfile offline = createOfflineProfile(username);
                cachedCallbackHandler.accept(offline);
                gameProfile[0] = offline;
            }
        };

        Runnable find = () -> profileRepo.findProfilesByNames(new String[]{username}, Agent.MINECRAFT, handler);
        if (async) {
            AkarinAsyncExecutor.scheduleAsyncTask(find);
        } else {
            find.run();
        }
        return gameProfile[0];
    }
    
    private static GameProfile createOfflineProfile(String username) {
        String usernameOffline = username.toLowerCase(Locale.ROOT);
        GameProfile offlineProfile = new GameProfile(EntityHuman.getOfflineUUID(usernameOffline), username);
        return offlineProfile;
    }

    public AkarinUserCache(GameProfileRepository repo, File file, Gson gson) {
        lastExpireDate = createExpireDate(true);
        
        this.profileHandler = repo;
        this.userCacheFile = file;
        this.gson = gson;

        this.load();
    }

    private GameProfile lookupAndCache(String username, @Nullable Consumer<GameProfile> callback, boolean async) {
        return lookupAndCache(username, callback, createExpireDate(false), async);
    }

    private GameProfile lookupAndCache(String username, @Nullable Consumer<GameProfile> callback, Date expireDate, boolean async) {
        Consumer<GameProfile> cachedCallbackHandler = profile -> {
            profiles.put(username, new UserCacheEntry(profile, expireDate));
            if (async)
                callback.accept(profile);
            
            if (!org.spigotmc.SpigotConfig.saveUserCacheOnStopOnly)
                save();
        };
        
        GameProfileRepository repository = MinecraftServer.getServer().getUserCache().gameProfileRepository();
        return lookup(repository != null ? repository : profileHandler, username, cachedCallbackHandler, async);
    }
    
    public GameProfile acquire(String username) {
        return acquire(username, null, false);
    }
    
    public GameProfile acquire(String username, @Nonnull Consumer<GameProfile> callback) {
        return acquire(username, callback, true);
    }
    
    public GameProfile acquire(String username, @Nullable Consumer<GameProfile> callback, boolean async) {
        if (StringUtils.isBlank(username))
            throw new UnsupportedOperationException("Blank username");
        
        if (!isOnlineMode())
            return createOfflineProfile(username);

        UserCacheEntry entry = profiles.getIfPresent(username);

        if (entry != null) {
            if (isExpired(entry)) {
                profiles.invalidate(username);
            } else {
                if (async)
                    callback.accept(entry.getProfile());
                
                return entry.getProfile();
            }
        }
        return lookupAndCache(username, callback, async);
    }

    @Nullable
    public GameProfile peek(String username) {
        if (!isOnlineMode())
            return createOfflineProfile(username);
        
        UserCacheEntry entry = profiles.getIfPresent(username);
        return entry == null ? null : entry.getProfile();
    }

    protected void offer(GameProfile profile) {
        if (isOnlineMode())
            offer(profile, createExpireDate(false));
    }

    private void offer(GameProfile profile, Date date) {
        String username = profile.getName();
        UserCacheEntry entry = profiles.getIfPresent(username);
        // Refresh expire date or create new entry
        entry = entry != null ? refreshExpireDate(entry) : new UserCacheEntry(profile, date);

        profiles.put(username, entry);
    }

    private void offer(UserCacheEntry entry) {
        if (!isExpired(entry))
            profiles.put(entry.getProfile().getName(), entry);
    }

    protected void load() {
        if (!isOnlineMode())
            return;
        
        BufferedReader reader = null;
        try {
            profiles.invalidateAll();
            
            reader = Files.newReader(userCacheFile, Charsets.UTF_8);
            List<UserCacheEntry> entries;
            synchronized (this.userCacheFile) {
                entries = this.gson.fromJson(reader, UserCache.PARAMETERIZED_TYPE);
            }
            
            if (entries != null && !entries.isEmpty())
                Lists.reverse(entries).forEach(this::offer);
        } catch (FileNotFoundException e) {
            ;
        } catch (JsonSyntaxException e) {
            LOGGER.warn("Usercache.json is corrupted or has bad formatting. Deleting it to prevent further issues.");
            this.userCacheFile.delete();
        } catch (JsonParseException e) {
            ;
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }
    
    protected void save() {
        if (!isOnlineMode())
            return;
        
        Runnable save = () -> {
            String jsonString = this.gson.toJson(this.entries());
            BufferedWriter writer = null;
            
            try {
                writer = Files.newWriter(this.userCacheFile, Charsets.UTF_8);
                synchronized (this.userCacheFile) {
                    writer.write(jsonString);
                }
            } catch (FileNotFoundException e) {
                ;
            } catch (IOException io) {
                ;
            } finally {
                IOUtils.closeQuietly(writer);
            }
        };
        
        AkarinAsyncExecutor.scheduleSingleAsyncTask(save);
    }

    private List<UserCacheEntry> entries() {
        return isOnlineMode() ? Lists.newArrayList(profiles.asMap().values()) : Collections.emptyList();
    }
}