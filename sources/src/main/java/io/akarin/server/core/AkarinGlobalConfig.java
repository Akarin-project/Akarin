package io.akarin.server.core;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

import io.akarin.api.internal.Akari;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

@SuppressWarnings("unused")
public class AkarinGlobalConfig {
    
    private static File CONFIG_FILE;
    private static final String HEADER = "This is the global configuration file for Akarin.\n"
            + "Some options may impact gameplay, so use with caution,\n"
            + "and make sure you know what each option does before configuring.\n"
            + "\n"
            + "Akarin forums: https://akarin.io/ \n";
    /*========================================================================*/
    public static YamlConfiguration config;
    static int version;
    /*========================================================================*/
    public static void init(File configFile) {
        CONFIG_FILE = configFile;
        config = new YamlConfiguration();
        try { 
            config.load(CONFIG_FILE);
        } catch (IOException ex) {
        } catch (InvalidConfigurationException ex) {
            Akari.logger.error("Could not load akarin.yml, please correct your syntax errors", ex);
            throw Throwables.propagate(ex);
        }
        config.options().header(HEADER);
        config.options().copyDefaults(true);

        version = getInt("config-version", 1);
        set("config-version", 1);
        readConfig(AkarinGlobalConfig.class, null);
    }

    static void readConfig(Class<?> clazz, Object instance) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isPrivate(method.getModifiers())) {
                if (method.getParameterTypes().length == 0 && method.getReturnType() == Void.TYPE) {
                    try {
                        method.setAccessible(true);
                        method.invoke(instance);
                    } catch (InvocationTargetException ex) {
                        throw Throwables.propagate(ex.getCause());
                    } catch (Exception ex) {
                        Akari.logger.error("Error invoking " + method, ex);
                    }
                }
            }
        }

        try {
            config.save(CONFIG_FILE);
        } catch (IOException ex) {
            Akari.logger.error("Could not save " + CONFIG_FILE, ex);
        }
    }

    private static final Pattern SPACE = Pattern.compile(" ");
    private static final Pattern NOT_NUMERIC = Pattern.compile("[^-\\d.]");
    public static int getSeconds(String str) {
        str = SPACE.matcher(str).replaceAll("");
        final char unit = str.charAt(str.length() - 1);
        str = NOT_NUMERIC.matcher(str).replaceAll("");
        double num;
        try {
            num = Double.parseDouble(str);
        } catch (Exception e) {
            num = 0D;
        }
        switch (unit) {
            case 'd': num *= (double) 60*60*24; break;
            case 'h': num *= (double) 60*60; break;
            case 'm': num *= 60; break;
            default: case 's': break;
        }
        return (int) num;
    }

    protected static String timeSummary(int seconds) {
        String time = "";

        if (seconds > 60 * 60 * 24) {
            time += TimeUnit.SECONDS.toDays(seconds) + "d";
            seconds %= 60 * 60 * 24;
        }

        if (seconds > 60 * 60) {
            time += TimeUnit.SECONDS.toHours(seconds) + "h";
            seconds %= 60 * 60;
        }

        if (seconds > 0) {
            time += TimeUnit.SECONDS.toMinutes(seconds) + "m";
        }
        return time;
    }

    public static void set(String path, Object val) {
        config.set(path, val);
    }

    private static boolean getBoolean(String path, boolean def) {
        config.addDefault(path, def);
        return config.getBoolean(path, config.getBoolean(path));
    }

    private static double getDouble(String path, double def) {
        config.addDefault(path, def);
        return config.getDouble(path, config.getDouble(path));
    }

    private static float getFloat(String path, float def) {
        // TODO: Figure out why getFloat() always returns the default value.
        return (float) getDouble(path, def);
    }

    private static int getInt(String path, int def) {
        config.addDefault(path, def);
        return config.getInt(path, config.getInt(path));
    }

    private static <T> List getList(String path, T def) {
        config.addDefault(path, def);
        return config.getList(path, config.getList(path));
    }

    private static String getString(String path, String def) {
        config.addDefault(path, def);
        return config.getString(path, config.getString(path));
    }
    /*========================================================================*/
    public static List<String> extraAddress;
    private static void extraAddress() {
        extraAddress = getList("bootstrap.extra-local-address", Lists.newArrayList());
    }
    
    public static boolean legacyVersioningCompat;
    private static void legacyVersioningCompat() {
        legacyVersioningCompat = getBoolean("alternative.legacy-versioning-compat", false);
    }
    
    public static int playersPerIOThread;
    private static void playersPerIOThread() {
        playersPerIOThread = getInt("core.players-per-chunk-io-thread", 50);
    }
    
    public static boolean silentAsyncTimings;
    private static void silentAsyncTimings() {
        silentAsyncTimings = getBoolean("core.always-silent-async-timing", false);
    }
    
    public static boolean legacyWorldTimings;
    private static void legacyWorldTimings() {
        legacyWorldTimings = getBoolean("alternative.legacy-world-timings-required", false);
    }
    
    public static int timeUpdateInterval;
    private static void timeUpdateInterval() {
        timeUpdateInterval = getSeconds(getString("core.world-time-update-interval", "1s"));
    }
    
    public static int keepAliveSendInterval;
    private static void keepAliveSendInterval() {
        keepAliveSendInterval = getSeconds(getString("core.keep-alive-packet-send-interval", "15s"));
    }
    
    public static int keepAliveTimeout;
    private static void keepAliveTimeout() {
        keepAliveTimeout = getSeconds(getString("core.keep-alive-response-timeout", "30s"));
    }
    
    public static int asyncLightingThreads;
    private static void asyncLightingThreads() {
        asyncLightingThreads = getInt("core.async-lighting.executor-threads", 2);
    }
    
    public static boolean asyncLightingWorkStealing;
    private static void asyncLightingWorkStealing() {
        asyncLightingWorkStealing = getBoolean("core.async-lighting.use-work-stealing", false);
    }
    
    public static boolean enableMockPlugin;
    private static void enableMockPlugin() {
        enableMockPlugin = getBoolean("core.thread-safe.enable-mock-plugins", false);
    }
    
    public static List<String> mockPackageList;
    private static void mockPluginList() {
        mockPackageList = getList("core.thread-safe.mock-package-name-contains", Lists.newArrayList("me.konsolas.aac"));
    }
    
    public static boolean enableAsyncCatcher;
    private static void enableAsyncCatcher() {
        enableAsyncCatcher = getBoolean("core.thread-safe.async-catcher.enable", false);
    }
    
    public static boolean throwOnAsyncCaught;
    private static void throwOnAsyncCaught() {
        throwOnAsyncCaught = getBoolean("core.thread-safe.async-catcher.throw-on-caught", true);
    }
    
    public static boolean allowSpawnerModify;
    private static void allowSpawnerModify() {
        allowSpawnerModify = getBoolean("alternative.allow-spawner-modify", true);
    }
    
    public static boolean noResponseDoGC;
    private static void noResponseDoGC() {
        noResponseDoGC = getBoolean("alternative.gc-before-stuck-restart", true);
    }
    
    public static String messageKick;
    private static void messageKick() {
        messageKick = getString("messages.disconnect.kick-player", "Kicked by an operator.");
    }
    
    public static String messageBan;
    private static void messageBan() {
        messageBan = getString("messages.disconnect.ban-player-name", "You are banned from this server! %s %s");
    }
    
    public static String messageBanReason;
    private static void messageBanReason() {
        messageBanReason = getString("messages.disconnect.ban-reason", "\nReason: ");
    }
    
    public static String messageBanExpires;
    private static void messageBanExpires() {
        messageBanExpires = getString("messages.disconnect.ban-expires", "\nYour ban will be removed on ");
    }
    
    public static String messageBanIp;
    private static void messageBanIp() {
        messageBanIp = getString("messages.disconnect.ban-player-ip", "Your IP address is banned from this server! %s %s");
    }
    
    public static String messageDupLogin;
    private static void messageDupLogin() {
        messageDupLogin = getString("messages.disconnect.kick-player-duplicate-login", "You logged in from another location");
    }
    
    public static String messageJoin;
    private static void messageJoin() {
        messageJoin = getString("messages.connect.player-join-server", "§e%s joined the game");
    }
    
    public static String messageJoinRenamed;
    private static void messageJoinRenamed() {
        messageJoinRenamed = getString("messages.connect.renamed-player-join-server", "§e%s (formerly known as %s) joined the game");
    }
    
    public static String messageKickKeepAlive;
    private static void messagekickKeepAlive() {
        messageKickKeepAlive = getString("messages.disconnect.kick-player-timeout-keep-alive", "Timed out");
    }
    
    public static String messagePlayerQuit;
    private static void messagePlayerQuit() {
        messagePlayerQuit = getString("messages.disconnect.player-quit-server", "§e%s left the game");
    }
    
    public static String serverBrandName;
    private static void serverBrandName() {
        serverBrandName = getString("alternative.modified-server-brand-name", "");
    }
    
    public static boolean disableEndPortalCreate;
    private static void disableEndPortalCreate() {
        disableEndPortalCreate = getBoolean("alternative.disable-end-portal-create", false);
    }
}
