/*
 * Copyright (c) 2018 Daniel Ennis (Aikar) MIT License
 */

package com.destroystokyo.paper;

import org.bukkit.Bukkit;
import org.bukkit.TestServer;
import org.junit.Test;

import java.util.logging.Level;

public class MaterialTagsTest {
    @Test
    public void testInitialize() {
        try {
            TestServer.getInstance();
            MaterialTags.SHULKER_BOXES.getValues();
            assert true;
        } catch (Throwable e) {
            Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
            assert false;
        }
    }
}
