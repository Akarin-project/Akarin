package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.yggdrasil.ProfileNotFoundException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NameReferencingFileConverter {

    private static final Logger e = LogManager.getLogger();
    public static final File a = new File("banned-ips.txt");
    public static final File b = new File("banned-players.txt");
    public static final File c = new File("ops.txt");
    public static final File d = new File("white-list.txt");

    static List<String> a(File file, Map<String, String[]> map) throws IOException {
        List<String> list = Files.readLines(file, StandardCharsets.UTF_8);
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            s = s.trim();
            if (!s.startsWith("#") && s.length() >= 1) {
                String[] astring = s.split("\\|");

                map.put(astring[0].toLowerCase(Locale.ROOT), astring);
            }
        }

        return list;
    }

    private static void a(MinecraftServer minecraftserver, Collection<String> collection, ProfileLookupCallback profilelookupcallback) {
        String[] astring = (String[]) collection.stream().filter((s) -> {
            return !UtilColor.b(s);
        }).toArray((i) -> {
            return new String[i];
        });

        if (minecraftserver.getOnlineMode()) {
            minecraftserver.getGameProfileRepository().findProfilesByNames(astring, Agent.MINECRAFT, profilelookupcallback);
        } else {
            String[] astring1 = astring;
            int i = astring.length;

            for (int j = 0; j < i; ++j) {
                String s = astring1[j];
                UUID uuid = EntityHuman.a(new GameProfile((UUID) null, s));
                GameProfile gameprofile = new GameProfile(uuid, s);

                profilelookupcallback.onProfileLookupSucceeded(gameprofile);
            }
        }

    }

    public static boolean a(final MinecraftServer minecraftserver) {
        final GameProfileBanList gameprofilebanlist = new GameProfileBanList(PlayerList.a);

        if (NameReferencingFileConverter.b.exists() && NameReferencingFileConverter.b.isFile()) {
            if (gameprofilebanlist.c().exists()) {
                try {
                    gameprofilebanlist.load();
                // CraftBukkit start - FileNotFoundException -> IOException, don't print stacktrace
                } catch (IOException filenotfoundexception) {
                    NameReferencingFileConverter.e.warn("Could not load existing file {}", gameprofilebanlist.c().getName());
                }
            }

            try {
                final Map<String, String[]> map = Maps.newHashMap();

                a(NameReferencingFileConverter.b, (Map) map);
                ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback() {
                    public void onProfileLookupSucceeded(GameProfile gameprofile) {
                        minecraftserver.getUserCache().a(gameprofile);
                        String[] astring = (String[]) map.get(gameprofile.getName().toLowerCase(Locale.ROOT));

                        if (astring == null) {
                            NameReferencingFileConverter.e.warn("Could not convert user banlist entry for {}", gameprofile.getName());
                            throw new NameReferencingFileConverter.FileConversionException("Profile not in the conversionlist");
                        } else {
                            Date date = astring.length > 1 ? NameReferencingFileConverter.b(astring[1], (Date) null) : null;
                            String s = astring.length > 2 ? astring[2] : null;
                            Date date1 = astring.length > 3 ? NameReferencingFileConverter.b(astring[3], (Date) null) : null;
                            String s1 = astring.length > 4 ? astring[4] : null;

                            gameprofilebanlist.add(new GameProfileBanEntry(gameprofile, date, s, date1, s1));
                        }
                    }

                    public void onProfileLookupFailed(GameProfile gameprofile, Exception exception) {
                        NameReferencingFileConverter.e.warn("Could not lookup user banlist entry for {}", gameprofile.getName(), exception);
                        if (!(exception instanceof ProfileNotFoundException)) {
                            throw new NameReferencingFileConverter.FileConversionException("Could not request user " + gameprofile.getName() + " from backend systems", exception);
                        }
                    }
                };

                a(minecraftserver, map.keySet(), profilelookupcallback);
                gameprofilebanlist.save();
                c(NameReferencingFileConverter.b);
                return true;
            } catch (IOException ioexception) {
                NameReferencingFileConverter.e.warn("Could not read old user banlist to convert it!", ioexception);
                return false;
            } catch (NameReferencingFileConverter.FileConversionException namereferencingfileconverter_fileconversionexception) {
                NameReferencingFileConverter.e.error("Conversion failed, please try again later", namereferencingfileconverter_fileconversionexception);
                return false;
            }
        } else {
            return true;
        }
    }

    public static boolean b(MinecraftServer minecraftserver) {
        IpBanList ipbanlist = new IpBanList(PlayerList.b);

        if (NameReferencingFileConverter.a.exists() && NameReferencingFileConverter.a.isFile()) {
            if (ipbanlist.c().exists()) {
                try {
                    ipbanlist.load();
                // CraftBukkit start - FileNotFoundException -> IOException, don't print stacktrace
                } catch (IOException filenotfoundexception) {
                    NameReferencingFileConverter.e.warn("Could not load existing file {}", ipbanlist.c().getName());
                }
            }

            try {
                Map<String, String[]> map = Maps.newHashMap();

                a(NameReferencingFileConverter.a, (Map) map);
                Iterator iterator = map.keySet().iterator();

                while (iterator.hasNext()) {
                    String s = (String) iterator.next();
                    String[] astring = (String[]) map.get(s);
                    Date date = astring.length > 1 ? b(astring[1], (Date) null) : null;
                    String s1 = astring.length > 2 ? astring[2] : null;
                    Date date1 = astring.length > 3 ? b(astring[3], (Date) null) : null;
                    String s2 = astring.length > 4 ? astring[4] : null;

                    ipbanlist.add(new IpBanEntry(s, date, s1, date1, s2));
                }

                ipbanlist.save();
                c(NameReferencingFileConverter.a);
                return true;
            } catch (IOException ioexception) {
                NameReferencingFileConverter.e.warn("Could not parse old ip banlist to convert it!", ioexception);
                return false;
            }
        } else {
            return true;
        }
    }

    public static boolean c(final MinecraftServer minecraftserver) {
        final OpList oplist = new OpList(PlayerList.c);

        if (NameReferencingFileConverter.c.exists() && NameReferencingFileConverter.c.isFile()) {
            if (oplist.c().exists()) {
                try {
                    oplist.load();
                // CraftBukkit start - FileNotFoundException -> IOException, don't print stacktrace
                } catch (IOException filenotfoundexception) {
                    NameReferencingFileConverter.e.warn("Could not load existing file {}", oplist.c().getName());
                }
            }

            try {
                List<String> list = Files.readLines(NameReferencingFileConverter.c, StandardCharsets.UTF_8);
                ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback() {
                    public void onProfileLookupSucceeded(GameProfile gameprofile) {
                        minecraftserver.getUserCache().a(gameprofile);
                        oplist.add(new OpListEntry(gameprofile, minecraftserver.j(), false));
                    }

                    public void onProfileLookupFailed(GameProfile gameprofile, Exception exception) {
                        NameReferencingFileConverter.e.warn("Could not lookup oplist entry for {}", gameprofile.getName(), exception);
                        if (!(exception instanceof ProfileNotFoundException)) {
                            throw new NameReferencingFileConverter.FileConversionException("Could not request user " + gameprofile.getName() + " from backend systems", exception);
                        }
                    }
                };

                a(minecraftserver, list, profilelookupcallback);
                oplist.save();
                c(NameReferencingFileConverter.c);
                return true;
            } catch (IOException ioexception) {
                NameReferencingFileConverter.e.warn("Could not read old oplist to convert it!", ioexception);
                return false;
            } catch (NameReferencingFileConverter.FileConversionException namereferencingfileconverter_fileconversionexception) {
                NameReferencingFileConverter.e.error("Conversion failed, please try again later", namereferencingfileconverter_fileconversionexception);
                return false;
            }
        } else {
            return true;
        }
    }

    public static boolean d(final MinecraftServer minecraftserver) {
        final WhiteList whitelist = new WhiteList(PlayerList.d);

        if (NameReferencingFileConverter.d.exists() && NameReferencingFileConverter.d.isFile()) {
            if (whitelist.c().exists()) {
                try {
                    whitelist.load();
                // CraftBukkit start - FileNotFoundException -> IOException, don't print stacktrace
                } catch (IOException filenotfoundexception) {
                    NameReferencingFileConverter.e.warn("Could not load existing file {}", whitelist.c().getName());
                }
            }

            try {
                List<String> list = Files.readLines(NameReferencingFileConverter.d, StandardCharsets.UTF_8);
                ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback() {
                    public void onProfileLookupSucceeded(GameProfile gameprofile) {
                        minecraftserver.getUserCache().a(gameprofile);
                        whitelist.add(new WhiteListEntry(gameprofile));
                    }

                    public void onProfileLookupFailed(GameProfile gameprofile, Exception exception) {
                        NameReferencingFileConverter.e.warn("Could not lookup user whitelist entry for {}", gameprofile.getName(), exception);
                        if (!(exception instanceof ProfileNotFoundException)) {
                            throw new NameReferencingFileConverter.FileConversionException("Could not request user " + gameprofile.getName() + " from backend systems", exception);
                        }
                    }
                };

                a(minecraftserver, list, profilelookupcallback);
                whitelist.save();
                c(NameReferencingFileConverter.d);
                return true;
            } catch (IOException ioexception) {
                NameReferencingFileConverter.e.warn("Could not read old whitelist to convert it!", ioexception);
                return false;
            } catch (NameReferencingFileConverter.FileConversionException namereferencingfileconverter_fileconversionexception) {
                NameReferencingFileConverter.e.error("Conversion failed, please try again later", namereferencingfileconverter_fileconversionexception);
                return false;
            }
        } else {
            return true;
        }
    }

    public static String a(final MinecraftServer minecraftserver, String s) {
        if (!UtilColor.b(s) && s.length() <= 16) {
            GameProfile gameprofile = minecraftserver.getUserCache().getProfile(s);

            if (gameprofile != null && gameprofile.getId() != null) {
                return gameprofile.getId().toString();
            } else if (!minecraftserver.H() && minecraftserver.getOnlineMode()) {
                final List<GameProfile> list = Lists.newArrayList();
                ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback() {
                    public void onProfileLookupSucceeded(GameProfile gameprofile1) {
                        minecraftserver.getUserCache().a(gameprofile1);
                        list.add(gameprofile1);
                    }

                    public void onProfileLookupFailed(GameProfile gameprofile1, Exception exception) {
                        NameReferencingFileConverter.e.warn("Could not lookup user whitelist entry for {}", gameprofile1.getName(), exception);
                    }
                };

                a(minecraftserver, Lists.newArrayList(new String[] { s}), profilelookupcallback);
                return !list.isEmpty() && ((GameProfile) list.get(0)).getId() != null ? ((GameProfile) list.get(0)).getId().toString() : "";
            } else {
                return EntityHuman.a(new GameProfile((UUID) null, s)).toString();
            }
        } else {
            return s;
        }
    }

    public static boolean a(final DedicatedServer dedicatedserver, PropertyManager propertymanager) {
        final File file = d(propertymanager);
        final File file1 = new File(file.getParentFile(), "playerdata");
        final File file2 = new File(file.getParentFile(), "unknownplayers");

        if (file.exists() && file.isDirectory()) {
            File[] afile = file.listFiles();
            List<String> list = Lists.newArrayList();
            File[] afile1 = afile;
            int i = afile.length;

            for (int j = 0; j < i; ++j) {
                File file3 = afile1[j];
                String s = file3.getName();

                if (s.toLowerCase(Locale.ROOT).endsWith(".dat")) {
                    String s1 = s.substring(0, s.length() - ".dat".length());

                    if (!s1.isEmpty()) {
                        list.add(s1);
                    }
                }
            }

            try {
                final String[] astring = (String[]) list.toArray(new String[list.size()]);
                ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback() {
                    public void onProfileLookupSucceeded(GameProfile gameprofile) {
                        dedicatedserver.getUserCache().a(gameprofile);
                        UUID uuid = gameprofile.getId();

                        if (uuid == null) {
                            throw new NameReferencingFileConverter.FileConversionException("Missing UUID for user profile " + gameprofile.getName());
                        } else {
                            this.a(file1, this.a(gameprofile), uuid.toString());
                        }
                    }

                    public void onProfileLookupFailed(GameProfile gameprofile, Exception exception) {
                        NameReferencingFileConverter.e.warn("Could not lookup user uuid for {}", gameprofile.getName(), exception);
                        if (exception instanceof ProfileNotFoundException) {
                            String s2 = this.a(gameprofile);

                            this.a(file2, s2, s2);
                        } else {
                            throw new NameReferencingFileConverter.FileConversionException("Could not request user " + gameprofile.getName() + " from backend systems", exception);
                        }
                    }

                    private void a(File file4, String s2, String s3) {
                        File file5 = new File(file, s2 + ".dat");
                        File file6 = new File(file4, s3 + ".dat");

                        // CraftBukkit start - Use old file name to seed lastKnownName
                        NBTTagCompound root = null;

                        try {
                            root = NBTCompressedStreamTools.a(new java.io.FileInputStream(file5));
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }

                        if (root != null) {
                            if (!root.hasKey("bukkit")) {
                                root.set("bukkit", new NBTTagCompound());
                            }
                            NBTTagCompound data = root.getCompound("bukkit");
                            data.setString("lastKnownName", s2);

                            try {
                                NBTCompressedStreamTools.a(root, new java.io.FileOutputStream(file2));
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                       }
                        // CraftBukkit end

                        NameReferencingFileConverter.b(file4);
                        if (!file5.renameTo(file6)) {
                            throw new NameReferencingFileConverter.FileConversionException("Could not convert file for " + s2);
                        }
                    }

                    private String a(GameProfile gameprofile) {
                        String s2 = null;
                        String[] astring1 = astring;
                        int k = astring1.length;

                        for (int l = 0; l < k; ++l) {
                            String s3 = astring1[l];

                            if (s3 != null && s3.equalsIgnoreCase(gameprofile.getName())) {
                                s2 = s3;
                                break;
                            }
                        }

                        if (s2 == null) {
                            throw new NameReferencingFileConverter.FileConversionException("Could not find the filename for " + gameprofile.getName() + " anymore");
                        } else {
                            return s2;
                        }
                    }
                };

                a(dedicatedserver, Lists.newArrayList(astring), profilelookupcallback);
                return true;
            } catch (NameReferencingFileConverter.FileConversionException namereferencingfileconverter_fileconversionexception) {
                NameReferencingFileConverter.e.error("Conversion failed, please try again later", namereferencingfileconverter_fileconversionexception);
                return false;
            }
        } else {
            return true;
        }
    }

    private static void b(File file) {
        if (file.exists()) {
            if (!file.isDirectory()) {
                throw new NameReferencingFileConverter.FileConversionException("Can't create directory " + file.getName() + " in world save directory.");
            }
        } else if (!file.mkdirs()) {
            throw new NameReferencingFileConverter.FileConversionException("Can't create directory " + file.getName() + " in world save directory.");
        }
    }

    public static boolean a(PropertyManager propertymanager) {
        boolean flag = b(propertymanager);

        flag = flag && c(propertymanager);
        return flag;
    }

    private static boolean b(PropertyManager propertymanager) {
        boolean flag = false;

        if (NameReferencingFileConverter.b.exists() && NameReferencingFileConverter.b.isFile()) {
            flag = true;
        }

        boolean flag1 = false;

        if (NameReferencingFileConverter.a.exists() && NameReferencingFileConverter.a.isFile()) {
            flag1 = true;
        }

        boolean flag2 = false;

        if (NameReferencingFileConverter.c.exists() && NameReferencingFileConverter.c.isFile()) {
            flag2 = true;
        }

        boolean flag3 = false;

        if (NameReferencingFileConverter.d.exists() && NameReferencingFileConverter.d.isFile()) {
            flag3 = true;
        }

        if (!flag && !flag1 && !flag2 && !flag3) {
            return true;
        } else {
            NameReferencingFileConverter.e.warn("**** FAILED TO START THE SERVER AFTER ACCOUNT CONVERSION!");
            NameReferencingFileConverter.e.warn("** please remove the following files and restart the server:");
            if (flag) {
                NameReferencingFileConverter.e.warn("* {}", NameReferencingFileConverter.b.getName());
            }

            if (flag1) {
                NameReferencingFileConverter.e.warn("* {}", NameReferencingFileConverter.a.getName());
            }

            if (flag2) {
                NameReferencingFileConverter.e.warn("* {}", NameReferencingFileConverter.c.getName());
            }

            if (flag3) {
                NameReferencingFileConverter.e.warn("* {}", NameReferencingFileConverter.d.getName());
            }

            return false;
        }
    }

    private static boolean c(PropertyManager propertymanager) {
        File file = d(propertymanager);

        if (file.exists() && file.isDirectory() && (file.list().length > 0 || !file.delete())) {
            NameReferencingFileConverter.e.warn("**** DETECTED OLD PLAYER DIRECTORY IN THE WORLD SAVE");
            NameReferencingFileConverter.e.warn("**** THIS USUALLY HAPPENS WHEN THE AUTOMATIC CONVERSION FAILED IN SOME WAY");
            NameReferencingFileConverter.e.warn("** please restart the server and if the problem persists, remove the directory '{}'", file.getPath());
            return false;
        } else {
            return true;
        }
    }

    private static File d(PropertyManager propertymanager) {
        String s = propertymanager.getString("level-name", "world");
        File file = new File(MinecraftServer.getServer().server.getWorldContainer(), s); // CraftBukkit - Respect container setting

        return new File(file, "players");
    }

    private static void c(File file) {
        File file1 = new File(file.getName() + ".converted");

        file.renameTo(file1);
    }

    private static Date b(String s, Date date) {
        Date date1;

        try {
            date1 = ExpirableListEntry.a.parse(s);
        } catch (ParseException parseexception) {
            date1 = date;
        }

        return date1;
    }

    static class FileConversionException extends RuntimeException {

        private FileConversionException(String s, Throwable throwable) {
            super(s, throwable);
        }

        private FileConversionException(String s) {
            super(s);
        }
    }
}
