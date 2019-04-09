package org.bukkit.command;

import org.bukkit.Server;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

public interface CommandSender extends Permissible {

    /**
     * Sends this sender a message
     *
     * @param message Message to be displayed
     */
    public void sendMessage(@NotNull String message);

    /**
     * Sends this sender multiple messages
     *
     * @param messages An array of messages to be displayed
     */
    public void sendMessage(@NotNull String[] messages);

    /**
     * Returns the server instance that this command is running on
     *
     * @return Server instance
     */
    @NotNull
    public Server getServer();

    /**
     * Gets the name of this command sender
     *
     * @return Name of the sender
     */
    @NotNull
    public String getName();

    // Spigot start
    public class Spigot
    {

        /**
         * Sends this sender a chat component.
         *
         * @param component the components to send
         */
        public void sendMessage(@NotNull net.md_5.bungee.api.chat.BaseComponent component) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        /**
         * Sends an array of components as a single message to the sender.
         *
         * @param components the components to send
         */
        public void sendMessage(@NotNull net.md_5.bungee.api.chat.BaseComponent... components) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    @NotNull
    Spigot spigot();
    // Spigot end

    // Paper start
    /**
     * Sends the component to the sender
     *
     * <p>If this sender does not support sending full components then
     * the component will be sent as legacy text.</p>
     *
     * @param component the component to send
     */
    default void sendMessage(@NotNull net.md_5.bungee.api.chat.BaseComponent component) {
        this.sendMessage(component.toLegacyText());
    }

    /**
     * Sends an array of components as a single message to the sender
     *
     * <p>If this sender does not support sending full components then
     * the components will be sent as legacy text.</p>
     *
     * @param components the components to send
     */
    default void sendMessage(@NotNull net.md_5.bungee.api.chat.BaseComponent... components) {
        this.sendMessage(new net.md_5.bungee.api.chat.TextComponent(components).toLegacyText());
    }
    // Paper end
}
