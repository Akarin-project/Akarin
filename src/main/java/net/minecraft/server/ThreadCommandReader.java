package net.minecraft.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ThreadCommandReader extends Thread {

    final MinecraftServer server;

    public ThreadCommandReader(MinecraftServer minecraftserver) {
        this.server = minecraftserver;
    }

    public void run() {
        jline.ConsoleReader bufferedreader = this.server.reader; // CraftBukkit
        String s = null;

        try {
            // CraftBukkit start - JLine disabling compatibility
            while (!this.server.isStopped && MinecraftServer.isRunning(this.server)) {
                if (org.bukkit.craftbukkit.Main.useJline) {
                    s = bufferedreader.readLine(">", null);
                } else {
                    s = bufferedreader.readLine();
                }
                if (s != null) {
                    this.server.issueCommand(s, this.server);
                }
                // CraftBukkit end
            }
        } catch (IOException ioexception) {
            // CraftBukkit
            java.util.logging.Logger.getLogger(ThreadCommandReader.class.getName()).log(java.util.logging.Level.SEVERE, null, ioexception);
        }
    }
}
