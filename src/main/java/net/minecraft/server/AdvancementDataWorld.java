package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdvancementDataWorld extends ResourceDataJson {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final Gson DESERIALIZER = (new GsonBuilder()).registerTypeHierarchyAdapter(Advancement.SerializedAdvancement.class, (com.google.gson.JsonDeserializer) (jsonelement, type, jsondeserializationcontext) -> {
        JsonObject jsonobject = ChatDeserializer.m(jsonelement, "advancement");

        return Advancement.SerializedAdvancement.a(jsonobject, jsondeserializationcontext);
    }).registerTypeAdapter(AdvancementRewards.class, new AdvancementRewards.b()).registerTypeHierarchyAdapter(IChatBaseComponent.class, new IChatBaseComponent.ChatSerializer()).registerTypeHierarchyAdapter(ChatModifier.class, new ChatModifier.ChatModifierSerializer()).registerTypeAdapterFactory(new ChatTypeAdapterFactory()).create();
    public Advancements REGISTRY = new Advancements();

    public AdvancementDataWorld() {
        super(AdvancementDataWorld.DESERIALIZER, "advancements");
    }

    protected void a(Map<MinecraftKey, JsonObject> map, IResourceManager iresourcemanager, GameProfilerFiller gameprofilerfiller) {
        Map<MinecraftKey, Advancement.SerializedAdvancement> map1 = Maps.newHashMap();

        map.forEach((minecraftkey, jsonobject) -> {
            // Spigot start
            if (org.spigotmc.SpigotConfig.disabledAdvancements != null && (org.spigotmc.SpigotConfig.disabledAdvancements.contains("*") || org.spigotmc.SpigotConfig.disabledAdvancements.contains(minecraftkey.toString()))) {
                return;
            }
            // Spigot end

            try {
                Advancement.SerializedAdvancement advancement_serializedadvancement = (Advancement.SerializedAdvancement) AdvancementDataWorld.DESERIALIZER.fromJson(jsonobject, Advancement.SerializedAdvancement.class);

                map1.put(minecraftkey, advancement_serializedadvancement);
            } catch (IllegalArgumentException | JsonParseException jsonparseexception) {
                AdvancementDataWorld.LOGGER.error("Parsing error loading custom advancement {}: {}", minecraftkey, jsonparseexception.getMessage());
            }

        });
        Advancements advancements = new Advancements();

        advancements.a((Map) map1);
        Iterator iterator = advancements.b().iterator();

        while (iterator.hasNext()) {
            Advancement advancement = (Advancement) iterator.next();

            if (advancement.c() != null) {
                AdvancementTree.a(advancement);
            }
        }

        this.REGISTRY = advancements;
    }

    @Nullable
    public Advancement a(MinecraftKey minecraftkey) {
        return this.REGISTRY.a(minecraftkey);
    }

    public Collection<Advancement> a() {
        return this.REGISTRY.c();
    }
}
