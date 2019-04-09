package com.destroystokyo.paper.utils;

import org.bukkit.plugin.PluginDescriptionFile;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * Prevents plugins (e.g. Essentials) from changing the parent of the plugin logger.
 */
public class PaperPluginLogger extends Logger {

    @NotNull
    public static Logger getLogger(@NotNull PluginDescriptionFile description) {
        Logger logger = new PaperPluginLogger(description);
        if (!LogManager.getLogManager().addLogger(logger)) {
            // Disable this if it's going to happen across reloads anyways...
            //logger.log(Level.WARNING, "Could not insert plugin logger - one was already found: {}", LogManager.getLogManager().getLogger(this.getName()));
            logger = LogManager.getLogManager().getLogger(description.getPrefix() != null ? description.getPrefix() : description.getName());
        }

        return logger;
    }

    private PaperPluginLogger(@NotNull PluginDescriptionFile description) {
        super(description.getPrefix() != null ? description.getPrefix() : description.getName(), null);
    }

    @Override
    public void setParent(@NotNull Logger parent) {
        if (getParent() != null) {
            warning("Ignoring attempt to change parent of plugin logger");
        } else {
            this.log(Level.FINE, "Setting plugin logger parent to {0}", parent);
            super.setParent(parent);
        }
    }

}
