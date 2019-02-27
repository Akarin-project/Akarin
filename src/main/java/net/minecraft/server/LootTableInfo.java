package net.minecraft.server;

import com.google.common.collect.Sets;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Set;
import javax.annotation.Nullable;

public class LootTableInfo {

    private final float a;
    public final int lootingMod; // CraftBukkit - add field
    private final WorldServer b;
    private final LootTableRegistry c;
    @Nullable
    private final Entity d;
    @Nullable
    private final EntityHuman e;
    @Nullable
    private final DamageSource f;
    @Nullable
    private final BlockPosition g;
    private final Set<LootTable> h = Sets.newLinkedHashSet();

    // CraftBukkit - add looting modifier to constructor
    public LootTableInfo(float f, WorldServer worldserver, LootTableRegistry loottableregistry, @Nullable Entity entity, @Nullable EntityHuman entityhuman, @Nullable DamageSource damagesource, @Nullable BlockPosition blockposition, int lootingModifier) {
        this.a = f;
        this.b = worldserver;
        this.c = loottableregistry;
        this.d = entity;
        this.e = entityhuman;
        this.f = damagesource;
        this.g = blockposition;
        this.lootingMod = lootingModifier; // CraftBukkit
    }

    @Nullable
    public Entity a() {
        return this.d;
    }

    @Nullable
    public Entity b() {
        return this.e;
    }

    @Nullable
    public Entity c() {
        return this.f == null ? null : this.f.getEntity();
    }

    @Nullable
    public BlockPosition e() {
        return this.g;
    }

    public boolean a(LootTable loottable) {
        return this.h.add(loottable);
    }

    public void b(LootTable loottable) {
        this.h.remove(loottable);
    }

    public LootTableRegistry f() {
        return this.c;
    }

    public float g() {
        return this.a;
    }

    public WorldServer h() {
        return this.b;
    }

    @Nullable
    public Entity a(LootTableInfo.EntityTarget loottableinfo_entitytarget) {
        switch (loottableinfo_entitytarget) {
        case THIS:
            return this.a();
        case KILLER:
            return this.c();
        case KILLER_PLAYER:
            return this.b();
        default:
            return null;
        }
    }

    public static enum EntityTarget {

        THIS("this"), KILLER("killer"), KILLER_PLAYER("killer_player");

        private final String d;

        private EntityTarget(String s) {
            this.d = s;
        }

        public static LootTableInfo.EntityTarget a(String s) {
            LootTableInfo.EntityTarget[] aloottableinfo_entitytarget = values();
            int i = aloottableinfo_entitytarget.length;

            for (int j = 0; j < i; ++j) {
                LootTableInfo.EntityTarget loottableinfo_entitytarget = aloottableinfo_entitytarget[j];

                if (loottableinfo_entitytarget.d.equals(s)) {
                    return loottableinfo_entitytarget;
                }
            }

            throw new IllegalArgumentException("Invalid entity target " + s);
        }

        public static class a extends TypeAdapter<LootTableInfo.EntityTarget> {

            public a() {}

            public void write(JsonWriter jsonwriter, LootTableInfo.EntityTarget loottableinfo_entitytarget) throws IOException {
                jsonwriter.value(loottableinfo_entitytarget.d);
            }

            public LootTableInfo.EntityTarget read(JsonReader jsonreader) throws IOException {
                return LootTableInfo.EntityTarget.a(jsonreader.nextString());
            }
        }
    }

    public static class Builder {

        private final WorldServer a;
        private float b;
        private int lootingMod = org.bukkit.loot.LootContext.DEFAULT_LOOT_MODIFIER; // CraftBukkit
        private Entity c;
        private EntityHuman d;
        private DamageSource e;
        private BlockPosition f;

        public Builder(WorldServer worldserver) {
            this.a = worldserver;
        }

        public LootTableInfo.Builder luck(float f) {
            this.b = f;
            return this;
        }

        public LootTableInfo.Builder entity(Entity entity) {
            this.c = entity;
            return this;
        }

        public LootTableInfo.Builder killer(EntityHuman entityhuman) {
            this.d = entityhuman;
            return this;
        }

        public LootTableInfo.Builder damageSource(DamageSource damagesource) {
            this.e = damagesource;
            return this;
        }

        public LootTableInfo.Builder position(BlockPosition blockposition) {
            this.f = blockposition;
            return this;
        }

        // CraftBukkit start - add looting modifier
        public LootTableInfo.Builder lootingModifier(int modifier) {
            this.lootingMod = modifier;
            return this;
        }
        // CraftBukkit end

        public LootTableInfo build() {
            return new LootTableInfo(this.b, this.a, this.a.getMinecraftServer().getLootTableRegistry(), this.c, this.d, this.e, this.f, this.lootingMod); // CraftBukkit add looting modifier
        }
    }
}
