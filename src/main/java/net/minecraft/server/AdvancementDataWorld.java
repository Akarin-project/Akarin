package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdvancementDataWorld implements IResourcePackListener {

    private static final Logger c = LogManager.getLogger();
    public static final Gson DESERIALIZER = (new GsonBuilder()).registerTypeHierarchyAdapter(Advancement.SerializedAdvancement.class, (com.google.gson.JsonDeserializer) (jsonelement, type, jsondeserializationcontext) -> {
        JsonObject jsonobject = ChatDeserializer.m(jsonelement, "advancement");

        return Advancement.SerializedAdvancement.a(jsonobject, jsondeserializationcontext);
    }).registerTypeAdapter(AdvancementRewards.class, new AdvancementRewards.b()).registerTypeHierarchyAdapter(IChatBaseComponent.class, new IChatBaseComponent.ChatSerializer()).registerTypeHierarchyAdapter(ChatModifier.class, new ChatModifier.ChatModifierSerializer()).registerTypeAdapterFactory(new ChatTypeAdapterFactory()).create();
    public static final Advancements REGISTRY = new Advancements();
    public static final int a = "advancements/".length();
    public static final int b = ".json".length();
    private boolean f;

    public AdvancementDataWorld() {}

    private Map<MinecraftKey, Advancement.SerializedAdvancement> b(IResourceManager iresourcemanager) {
        Map<MinecraftKey, Advancement.SerializedAdvancement> map = Maps.newHashMap();
        Iterator iterator = iresourcemanager.a("advancements", (s) -> {
            return s.endsWith(".json");
        }).iterator();

        while (iterator.hasNext()) {
            MinecraftKey minecraftkey = (MinecraftKey) iterator.next();
            String s = minecraftkey.getKey();
            MinecraftKey minecraftkey1 = new MinecraftKey(minecraftkey.b(), s.substring(AdvancementDataWorld.a, s.length() - AdvancementDataWorld.b));

            try {
                IResource iresource = iresourcemanager.a(minecraftkey);
                Throwable throwable = null;
                // Spigot start
                if (org.spigotmc.SpigotConfig.disabledAdvancements != null && (org.spigotmc.SpigotConfig.disabledAdvancements.contains("*") || org.spigotmc.SpigotConfig.disabledAdvancements.contains(minecraftkey.toString()))) {
                    continue;
                }
                // Spigot end

                try {
                    Advancement.SerializedAdvancement advancement_serializedadvancement = (Advancement.SerializedAdvancement) ChatDeserializer.a(AdvancementDataWorld.DESERIALIZER, IOUtils.toString(iresource.b(), StandardCharsets.UTF_8), Advancement.SerializedAdvancement.class);

                    if (advancement_serializedadvancement == null) {
                        AdvancementDataWorld.c.error("Couldn't load custom advancement {} from {} as it's empty or null", minecraftkey1, minecraftkey);
                    } else {
                        map.put(minecraftkey1, advancement_serializedadvancement);
                    }
                } catch (Throwable throwable1) {
                    throwable = throwable1;
                    throw throwable1;
                } finally {
                    if (iresource != null) {
                        if (throwable != null) {
                            try {
                                iresource.close();
                            } catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        } else {
                            iresource.close();
                        }
                    }

                }
            } catch (IllegalArgumentException | JsonParseException jsonparseexception) {
                AdvancementDataWorld.c.error("Parsing error loading custom advancement {}: {}", minecraftkey1, jsonparseexception.getMessage());
                this.f = true;
            } catch (IOException ioexception) {
                AdvancementDataWorld.c.error("Couldn't read custom advancement {} from {}", minecraftkey1, minecraftkey, ioexception);
                this.f = true;
            }
        }

        return map;
    }

    @Nullable
    public Advancement a(MinecraftKey minecraftkey) {
        return AdvancementDataWorld.REGISTRY.a(minecraftkey);
    }

    public Collection<Advancement> b() {
        return AdvancementDataWorld.REGISTRY.c();
    }

    public void a(IResourceManager iresourcemanager) {
        this.f = false;
        AdvancementDataWorld.REGISTRY.a();
        Map<MinecraftKey, Advancement.SerializedAdvancement> map = this.b(iresourcemanager);

        AdvancementDataWorld.REGISTRY.a(map);
        Iterator iterator = AdvancementDataWorld.REGISTRY.b().iterator();

        while (iterator.hasNext()) {
            Advancement advancement = (Advancement) iterator.next();

            if (advancement.c() != null) {
                AdvancementTree.a(advancement);
            }
        }

    }
}
