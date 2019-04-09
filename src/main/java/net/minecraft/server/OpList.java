package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.util.Iterator;

import org.bukkit.Bukkit;

public class OpList extends JsonList<GameProfile, OpListEntry> {

    public OpList(File file) {
        super(file);
    }

    protected JsonListEntry<GameProfile> a(JsonObject jsonobject) {
        return new OpListEntry(jsonobject);
    }

    public String[] getEntries() {
        String[] astring = new String[this.e().size()];
        int i = 0;

        JsonListEntry jsonlistentry;

        for (Iterator iterator = this.e().iterator(); iterator.hasNext(); astring[i++] = ((GameProfile) jsonlistentry.getKey()).getName()) {
            jsonlistentry = (JsonListEntry) iterator.next();
        }

        return astring;
    }

    public boolean b(GameProfile gameprofile) {
        OpListEntry oplistentry = (OpListEntry) this.get(gameprofile);

        return oplistentry != null ? oplistentry.b() : false;
    }

    protected String a(GameProfile gameprofile) {
        return AkarinUserCache.isOnlineMode() ? gameprofile.getId().toString() : gameprofile.getName(); // Akarin
    }
}
