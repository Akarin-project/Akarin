package net.minecraft.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import joptsimple.OptionSet; // CraftBukkit

public class PropertyManager {

    private static final Logger a = LogManager.getLogger();
    public final Properties properties = new Properties();
    private final File file;

    public PropertyManager(File file) {
        this.file = file;
        if (file.exists()) {
            FileInputStream fileinputstream = null;

            try {
                fileinputstream = new FileInputStream(file);
                this.properties.load(fileinputstream);
            } catch (Exception exception) {
                PropertyManager.a.warn("Failed to load {}", file, exception);
                this.a();
            } finally {
                if (fileinputstream != null) {
                    try {
                        fileinputstream.close();
                    } catch (IOException ioexception) {
                        ;
                    }
                }

            }
        } else {
            PropertyManager.a.warn("{} does not exist", file);
            this.a();
        }

    }

    // CraftBukkit start
    private OptionSet options = null;

    public PropertyManager(final OptionSet options) {
        this((File) options.valueOf("config"));

        this.options = options;
    }

    private <T> T getOverride(String name, T value) {
        if ((this.options != null) && (this.options.has(name))) {
            return (T) this.options.valueOf(name);
        }

        return value;
    }
    // CraftBukkit end

    public void a() {
        PropertyManager.a.info("Generating new properties file");
        this.savePropertiesFile();
    }

    public void savePropertiesFile() {
        FileOutputStream fileoutputstream = null;

        try {
            // CraftBukkit start - Don't attempt writing to file if it's read only
            if (this.file.exists() && !this.file.canWrite()) {
                return;
            }
            // CraftBukkit end

            fileoutputstream = new FileOutputStream(this.file);
            this.properties.store(fileoutputstream, "Minecraft server properties");
        } catch (Exception exception) {
            PropertyManager.a.warn("Failed to save {}", this.file, exception);
            this.a();
        } finally {
            if (fileoutputstream != null) {
                try {
                    fileoutputstream.close();
                } catch (IOException ioexception) {
                    ;
                }
            }

        }

    }

    public File c() {
        return this.file;
    }

    public String getString(String s, String s1) {
        if (!this.properties.containsKey(s)) {
            this.properties.setProperty(s, s1);
            this.savePropertiesFile();
            this.savePropertiesFile();
        }

        return getOverride(s, this.properties.getProperty(s, s1)); // CraftBukkit
    }

    public int getInt(String s, int i) {
        try {
            return getOverride(s, Integer.parseInt(this.getString(s, "" + i))); // CraftBukkit
        } catch (Exception exception) {
            this.properties.setProperty(s, "" + i);
            this.savePropertiesFile();
            return getOverride(s, i); // CraftBukkit
        }
    }

    public long getLong(String s, long i) {
        try {
            return getOverride(s, Long.parseLong(this.getString(s, "" + i))); // CraftBukkit
        } catch (Exception exception) {
            this.properties.setProperty(s, "" + i);
            this.savePropertiesFile();
            return getOverride(s, i); // CraftBukkit
        }
    }

    public boolean getBoolean(String s, boolean flag) {
        try {
            return getOverride(s, Boolean.parseBoolean(this.getString(s, "" + flag))); //CraftBukkit
        } catch (Exception exception) {
            this.properties.setProperty(s, "" + flag);
            this.savePropertiesFile();
            return getOverride(s, flag); // CraftBukkit
        }
    }

    public void setProperty(String s, Object object) {
        this.properties.setProperty(s, "" + object);
    }

    public boolean a(String s) {
        return this.properties.containsKey(s);
    }

    public void b(String s) {
        this.properties.remove(s);
    }
}
