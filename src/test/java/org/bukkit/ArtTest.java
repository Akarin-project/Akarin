package org.bukkit;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.server.IRegistry;
import net.minecraft.server.MinecraftKey;
import net.minecraft.server.Paintings;

import org.bukkit.craftbukkit.CraftArt;
import org.junit.Test;

import com.google.common.collect.Lists;
import org.bukkit.support.AbstractTestingBase;

public class ArtTest extends AbstractTestingBase {
    private static final int UNIT_MULTIPLIER = 16;

    @Test
    public void verifyMapping() {
        List<Art> arts = Lists.newArrayList(Art.values());

        for (MinecraftKey key : IRegistry.MOTIVE.keySet()) {
            Paintings enumArt = IRegistry.MOTIVE.get(key);
            String name = key.getKey();
            int width = enumArt.b() / UNIT_MULTIPLIER;
            int height = enumArt.c() / UNIT_MULTIPLIER;

            Art subject = CraftArt.NotchToBukkit(enumArt);

            String message = String.format("org.bukkit.Art is missing '%s'", name);
            assertNotNull(message, subject);

            assertThat(Art.getByName(name), is(subject));
            assertThat("Art." + subject + "'s width", subject.getBlockWidth(), is(width));
            assertThat("Art." + subject + "'s height", subject.getBlockHeight(), is(height));

            arts.remove(subject);
        }

        assertThat("org.bukkit.Art has too many arts", arts, is(Collections.EMPTY_LIST));
    }

    @Test
    public void testCraftArtToNotch() {
        Map<Paintings, Art> cache = new HashMap<>();
        for (Art art : Art.values()) {
            Paintings enumArt = CraftArt.BukkitToNotch(art);
            assertNotNull(art.name(), enumArt);
            assertThat(art.name(), cache.put(enumArt, art), is(nullValue()));
        }
    }

    @Test
    public void testCraftArtToBukkit() {
        Map<Art, Paintings> cache = new EnumMap(Art.class);
        for (Paintings enumArt : (Iterable<Paintings>) IRegistry.MOTIVE) { // Eclipse fail
            Art art = CraftArt.NotchToBukkit(enumArt);
            assertNotNull("Could not CraftArt.NotchToBukkit " + enumArt, art);
            assertThat("Duplicate artwork " + enumArt, cache.put(art, enumArt), is(nullValue()));
        }
    }
}
