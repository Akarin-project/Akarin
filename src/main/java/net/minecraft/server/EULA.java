package net.minecraft.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EULA {

    private static final Logger a = LogManager.getLogger();
    private final File b;
    private final boolean c;

    public EULA(File file) {
        this.b = file;
        this.c = SharedConstants.b || this.a(file);
    }

    private boolean a(File file) {
        FileInputStream fileinputstream = null;
        boolean flag = false;

        try {
            Properties properties = new Properties();

            fileinputstream = new FileInputStream(file);
            properties.load(fileinputstream);
            flag = Boolean.parseBoolean(properties.getProperty("eula", "false"));
        } catch (Exception exception) {
            EULA.a.warn("Failed to load {}", file);
            this.b();
        } finally {
            IOUtils.closeQuietly(fileinputstream);
        }

        return flag;
    }

    public boolean a() {
        return this.c;
    }

    public void b() {
        if (!SharedConstants.b) {
            FileOutputStream fileoutputstream = null;

            try {
                Properties properties = new Properties();

                fileoutputstream = new FileOutputStream(this.b);
                properties.setProperty("eula", "false");
                properties.store(fileoutputstream, "By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula).");
            } catch (Exception exception) {
                EULA.a.warn("Failed to save {}", this.b, exception);
            } finally {
                IOUtils.closeQuietly(fileoutputstream);
            }

        }
    }
}
