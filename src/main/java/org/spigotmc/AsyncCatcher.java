package org.spigotmc;

import co.aikar.timings.ThreadAssertion;
import net.minecraft.server.MinecraftServer;

public class AsyncCatcher
{

    public static boolean enabled = true;
    public static boolean shuttingDown = false; // Paper

    public static void catchOp(String reason)
    {
        if ( enabled && !ThreadAssertion.is() && Thread.currentThread() != MinecraftServer.getServer().primaryThread )
        {
            throw new IllegalStateException( "Asynchronous " + reason + "!" );
        }
    }
}
