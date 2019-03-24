package net.minecraft.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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
import io.akarin.server.core.AkarinGlobalConfig;
import net.minecraft.server.UserCache.UserCacheEntry;

public class AkarinUserCache {
    private final static Logger LOGGER = LogManager.getLogger("Akarin");
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    
    // Used to reduce date create
    private final static long RECREATE_DATE_INTERVAL = TimeUnit.MILLISECONDS.convert(10, TimeUnit.MINUTES);
    private static long lastWarpExpireDate;
    private static Date lastExpireDate;
    
    /**
     * All user caches, Username -> Entry(profile and expire date included)
     */
    private final Cache<String, UserCacheEntry> profiles = Caffeine.newBuilder().maximumSize(SpigotConfig.userCacheCap).build();
    
    private final GameProfileRepository profileHandler;
    protected final Gson gson;
    private final File userCacheFile;
    
    public static boolean isOnlineMode() {
        return UserCache.isOnlineMode() || (SpigotConfig.bungee && PaperConfig.bungeeOnlineMode);
    }

    public static Date createExpireDate(boolean force) {
        long now = System.currentTimeMillis();
        if (force || (now - lastWarpExpireDate) > RECREATE_DATE_INTERVAL) {
            lastWarpExpireDate = now;
            Calendar calendar = Calendar.getInstance();
            
            calendar.setTimeInMillis(now);
            calendar.add(Calendar.DAY_OF_YEAR, AkarinGlobalConfig.userCacheExpireDays);
            return lastExpireDate = calendar.getTime();
        }

        return lastExpireDate;
    }

    public static boolean isExpired(UserCacheEntry entry) {
        return System.currentTimeMillis() >= entry.getExpireDate().getTime();
    }

    public static UserCacheEntry refreshExpireDate(UserCacheEntry entry) {
        return new UserCacheEntry(entry.getProfile(), createExpireDate(true));
    }

    public static GameProfile lookup(GameProfileRepository profileRepo, String keyUsername, ProfileLookupCallback callback, boolean async) {
        if (!isOnlineMode())
            callback.onProfileLookupSucceeded(new GameProfile(EntityHuman.getOfflineUUID(keyUsername.toLowerCase(Locale.ROOT)), keyUsername));

        GameProfile[] gameProfile = new GameProfile[1];
        ProfileLookupCallback callbackHandler = new ProfileLookupCallback() {
            @Override
            public void onProfileLookupSucceeded(GameProfile gameprofile) {
                LOGGER.warn("Failed to lookup player {}, using local UUID.", gameprofile.getName());
                if (async)
                    callback.onProfileLookupSucceeded(gameprofile);
                else
                    gameProfile[0] = gameprofile;
            }

            @Override
            public void onProfileLookupFailed(GameProfile gameprofile, Exception ex) {
                LOGGER.warn("Failed to lookup player {}, using local UUID.", gameprofile.getName());
                if (async)
                    callback.onProfileLookupSucceeded(new GameProfile(EntityHuman.getOfflineUUID(keyUsername), keyUsername));
                else
                    gameProfile[0] = new GameProfile(EntityHuman.getOfflineUUID(keyUsername), keyUsername);
            }
        };

        Runnable find = () -> profileRepo.findProfilesByNames(new String[] { keyUsername }, Agent.MINECRAFT, callbackHandler);
        if (async) {
            MCUtil.scheduleAsyncTask(find);
            return null; // TODO: future
        } else {
            find.run();
            return gameProfile[0];
        }
    }

    public AkarinUserCache(GameProfileRepository repo, File file, Gson gson) {
        lastExpireDate = createExpireDate(true);
        
        this.profileHandler = repo;
        this.userCacheFile = file;
        this.gson = gson;

        this.load();
    }

    GameProfile lookupAndCache(String keyUsername, ProfileLookupCallback callback, boolean async) {
        return lookupAndCache(keyUsername, callback, createExpireDate(false), async);
    }

    GameProfile lookupAndCache(String keyUsername, ProfileLookupCallback callback, Date date, boolean async) {
        ProfileLookupCallback callbackHandler = new ProfileLookupCallback() {
            @Override
            public void onProfileLookupSucceeded(GameProfile gameprofile) {
                profiles.put(keyUsername, new UserCacheEntry(gameprofile, date));
                if (async)
                    callback.onProfileLookupSucceeded(gameprofile);
                
                if(!org.spigotmc.SpigotConfig.saveUserCacheOnStopOnly)
                    save();
            }

            @Override
            public void onProfileLookupFailed(GameProfile gameprofile, Exception ex) {
                ;
            }
        };
        
        return lookup(profileHandler, keyUsername, callbackHandler, async);
    }
    
    public GameProfile acquire(String username) {
        return acquire(username, null, false);
    }
    
    public GameProfile acquire(String username, ProfileLookupCallback callback) {
        return acquire(username, callback, true);
    }
    
    public GameProfile acquire(String username, ProfileLookupCallback callback, boolean async) {
        if (StringUtils.isBlank(username))
            return null;

        String keyUsername = isOnlineMode() ? username : username.toLowerCase(Locale.ROOT);
        UserCacheEntry entry = profiles.getIfPresent(keyUsername);

        if (entry != null) {
            if (isExpired(entry)) {
                profiles.invalidate(keyUsername);
                return lookupAndCache(keyUsername, callback, async);
            } else {
                if (async) {
                    callback.onProfileLookupSucceeded(entry.getProfile());
                    return null;
                } else {
                    return entry.getProfile();
                }
            }
        }
        return lookupAndCache(keyUsername, callback, async);
    }
    
    @Nullable
    public GameProfile peek(String username) {
        String keyUsername = isOnlineMode() ? username : username.toLowerCase(Locale.ROOT);
        UserCacheEntry entry = profiles.getIfPresent(keyUsername);
        return entry == null ? null : entry.getProfile();
    }

    void offer(GameProfile profile) {
        offer(profile, createExpireDate(false));
    }

    void offer(GameProfile profile, Date date) {
        String keyUsername = isOnlineMode() ? profile.getName() : profile.getName().toLowerCase(Locale.ROOT);
        UserCacheEntry entry = profiles.getIfPresent(keyUsername);

        if (entry != null) {
            // The offered profile may has an raw case, this only happened on offline servers with old caches,
            // so replace with an lower-case profile.
            if (!UserCache.isOnlineMode() && !entry.getProfile().getName().equals(profile.getName()))
                entry = new UserCacheEntry(new GameProfile(entry.getProfile().getId(), keyUsername), date);
            else
                entry = refreshExpireDate(entry);
        } else {
            entry = new UserCacheEntry(profile, date);
        }

        profiles.put(keyUsername, entry);
        if (!SpigotConfig.saveUserCacheOnStopOnly)
            this.save();
    }

    void offer(UserCacheEntry entry) {
        if (!isExpired(entry))
            profiles.put(isOnlineMode() ? entry.getProfile().getName() : entry.getProfile().getName().toLowerCase(Locale.ROOT), entry);
    }

    String[] usernames() {
        return profiles.asMap().keySet().toArray(new String[profiles.asMap().size()]);
    }

    protected void load() {
        BufferedReader reader = null;

        try {
            reader = Files.newReader(userCacheFile, Charsets.UTF_8);
            List<UserCacheEntry> entries = this.gson.fromJson(reader, UserCache.PARAMETERIZED_TYPE);
            profiles.invalidateAll();

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
    
    public void save() {
        save(true);
    }
    
    public void save(boolean async) {
        Runnable save = () -> {
            String jsonString = this.gson.toJson(this.entries());
            BufferedWriter writer = null;
            
            try {
                writer = Files.newWriter(this.userCacheFile, Charsets.UTF_8);
                writer.write(jsonString);
                return;
            } catch (FileNotFoundException e) {
                return;
            } catch (IOException io) {
                ;
            } finally {
                IOUtils.closeQuietly(writer);
            }
        };
        
        if (async)
            MCUtil.scheduleAsyncTask(save);
        else
            save.run();
    }

    List<UserCacheEntry> entries() {
        return Lists.newArrayList(profiles.asMap().values());
    }
}