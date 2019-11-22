package net.minecraft.server;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import java.util.Set;

public class LootItemConditionSurvivesExplosion implements LootItemCondition {

    private static final LootItemConditionSurvivesExplosion a = new LootItemConditionSurvivesExplosion();

    private LootItemConditionSurvivesExplosion() {}

    @Override
    public Set<LootContextParameter<?>> a() {
        return ImmutableSet.of(LootContextParameters.EXPLOSION_RADIUS);
    }

    public boolean test(LootTableInfo loottableinfo) {
        Float ofloat = (Float) loottableinfo.getContextParameter(LootContextParameters.EXPLOSION_RADIUS);

        if (ofloat != null) {
            Random random = loottableinfo.b();
            float f = 1.0F / ofloat;

            // CraftBukkit - <= to < to allow for plugins to completely disable block drops from explosions
            return random.nextFloat() < f;
        } else {
            return true;
        }
    }

    public static LootItemCondition.a b() {
        return () -> {
            return LootItemConditionSurvivesExplosion.a;
        };
    }

    public static class a extends LootItemCondition.b<LootItemConditionSurvivesExplosion> {

        protected a() {
            super(new MinecraftKey("survives_explosion"), LootItemConditionSurvivesExplosion.class);
        }

        public void a(JsonObject jsonobject, LootItemConditionSurvivesExplosion lootitemconditionsurvivesexplosion, JsonSerializationContext jsonserializationcontext) {}

        @Override
        public LootItemConditionSurvivesExplosion b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            return LootItemConditionSurvivesExplosion.a;
        }
    }
}
