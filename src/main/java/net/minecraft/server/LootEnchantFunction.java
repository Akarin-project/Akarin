package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;

public class LootEnchantFunction extends LootItemFunction {

    private final LootValueBounds a;
    private final int b;

    public LootEnchantFunction(LootItemCondition[] alootitemcondition, LootValueBounds lootvaluebounds, int i) {
        super(alootitemcondition);
        this.a = lootvaluebounds;
        this.b = i;
    }

    public ItemStack a(ItemStack itemstack, Random random, LootTableInfo loottableinfo) {
        Entity entity = loottableinfo.c();

        if (entity instanceof EntityLiving) {
            int i = EnchantmentManager.g((EntityLiving) entity);
            // CraftBukkit start - use lootingModifier if set by plugin
            if (loottableinfo.lootingMod > org.bukkit.loot.LootContext.DEFAULT_LOOT_MODIFIER) {
                i = loottableinfo.lootingMod;
            }
            // CraftBukkit end

            if (i <= 0) { // CraftBukkit - account for possible negative looting values from Bukkit
                return itemstack;
            }

            float f = (float) i * this.a.b(random);

            itemstack.add(Math.round(f));
            if (this.b != 0 && itemstack.getCount() > this.b) {
                itemstack.setCount(this.b);
            }
        }

        return itemstack;
    }

    public static class a extends LootItemFunction.a<LootEnchantFunction> {

        protected a() {
            super(new MinecraftKey("looting_enchant"), LootEnchantFunction.class);
        }

        public void a(JsonObject jsonobject, LootEnchantFunction lootenchantfunction, JsonSerializationContext jsonserializationcontext) {
            jsonobject.add("count", jsonserializationcontext.serialize(lootenchantfunction.a));
            if (lootenchantfunction.b > 0) {
                jsonobject.add("limit", jsonserializationcontext.serialize(lootenchantfunction.b));
            }

        }

        public LootEnchantFunction b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            int i = ChatDeserializer.a(jsonobject, "limit", 0);

            return new LootEnchantFunction(alootitemcondition, (LootValueBounds) ChatDeserializer.a(jsonobject, "count", jsondeserializationcontext, LootValueBounds.class), i);
        }
    }
}
