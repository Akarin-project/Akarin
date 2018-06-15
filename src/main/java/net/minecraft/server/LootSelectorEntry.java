package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Random;

public abstract class LootSelectorEntry {

    protected final int c; public int getWeight() { return c; } // Paper - OBFHELPER
    protected final int d; public int getQuality() { return d; } // Paper - OBFHELPER
    protected final LootItemCondition[] e;

    protected LootSelectorEntry(int i, int j, LootItemCondition[] alootitemcondition) {
        this.c = i;
        this.d = j;
        this.e = alootitemcondition;
    }

    public int a(float f) {
        // Paper start - Offer an alternative loot formula to refactor how luck bonus applies
        // SEE: https://luckformula.emc.gs for details and data
        if (lastLuck != null && lastLuck == f) {
            return lastWeight;
        }
        // This is vanilla
        float qualityModifer = (float) this.getQuality() * f;
        double baseWeight = (this.getWeight() + qualityModifer);
        if (com.destroystokyo.paper.PaperConfig.useAlternativeLuckFormula) {
            // Random boost to avoid losing precision in the final int cast on return
            final int weightBoost = 100;
            baseWeight *= weightBoost;
            // If we have vanilla 1, bump that down to 0 so nothing is is impacted
            // vanilla 3 = 300, 200 basis = impact 2%
            // =($B2*(($B2-100)/100/100))
            double impacted = baseWeight * ((baseWeight - weightBoost) / weightBoost / 100);
            // =($B$7/100)
            float luckModifier = Math.min(100, f * 10) / 100;
            // =B2 - (C2 *($B$7/100))
            baseWeight = Math.ceil(baseWeight - (impacted * luckModifier));
        }
        lastLuck = f;
        lastWeight = (int) Math.max(0, Math.floor(baseWeight));
        return lastWeight;
    }
    private Float lastLuck = null;
    private int lastWeight = 0;
    // Paper end

    public abstract void a(Collection<ItemStack> collection, Random random, LootTableInfo loottableinfo);

    protected abstract void a(JsonObject jsonobject, JsonSerializationContext jsonserializationcontext);

    public static class a implements JsonDeserializer<LootSelectorEntry>, JsonSerializer<LootSelectorEntry> {

        public a() {}

        public LootSelectorEntry deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "loot item");
            String s = ChatDeserializer.h(jsonobject, "type");
            int i = ChatDeserializer.a(jsonobject, "weight", 1);
            int j = ChatDeserializer.a(jsonobject, "quality", 0);
            LootItemCondition[] alootitemcondition;

            if (jsonobject.has("conditions")) {
                alootitemcondition = (LootItemCondition[]) ChatDeserializer.a(jsonobject, "conditions", jsondeserializationcontext, LootItemCondition[].class);
            } else {
                alootitemcondition = new LootItemCondition[0];
            }

            if ("item".equals(s)) {
                return LootItem.a(jsonobject, jsondeserializationcontext, i, j, alootitemcondition);
            } else if ("loot_table".equals(s)) {
                return LootSelectorLootTable.a(jsonobject, jsondeserializationcontext, i, j, alootitemcondition);
            } else if ("empty".equals(s)) {
                return LootSelectorEmpty.a(jsonobject, jsondeserializationcontext, i, j, alootitemcondition);
            } else {
                throw new JsonSyntaxException("Unknown loot entry type '" + s + "'");
            }
        }

        public JsonElement serialize(LootSelectorEntry lootselectorentry, Type type, JsonSerializationContext jsonserializationcontext) {
            JsonObject jsonobject = new JsonObject();

            jsonobject.addProperty("weight", lootselectorentry.c);
            jsonobject.addProperty("quality", lootselectorentry.d);
            if (lootselectorentry.e.length > 0) {
                jsonobject.add("conditions", jsonserializationcontext.serialize(lootselectorentry.e));
            }

            if (lootselectorentry instanceof LootItem) {
                jsonobject.addProperty("type", "item");
            } else if (lootselectorentry instanceof LootSelectorLootTable) {
                jsonobject.addProperty("type", "loot_table");
            } else {
                if (!(lootselectorentry instanceof LootSelectorEmpty)) {
                    throw new IllegalArgumentException("Don't know how to serialize " + lootselectorentry);
                }

                jsonobject.addProperty("type", "empty");
            }

            lootselectorentry.a(jsonobject, jsonserializationcontext);
            return jsonobject;
        }
    }
}
