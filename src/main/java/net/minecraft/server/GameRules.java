package net.minecraft.server;

import com.google.common.collect.Maps;
import com.koloboke.collect.map.hash.HashObjObjMap;
import com.koloboke.collect.map.hash.HashObjObjMaps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public class GameRules {

    // Paper start - Optimize GameRules
    private static final int RULES_SIZE = 256;

    private static <K, V> java.util.LinkedHashMap<K, V> linkedMapOf(final int capacity, final TreeMap<K, V> map) {
        final java.util.LinkedHashMap<K, V> ret = new java.util.LinkedHashMap<>(capacity);
        ret.putAll(map);
        return ret;
    }

    private static final java.util.LinkedHashMap<String, GameRuleDefinition> a = GameRules.linkedMapOf(RULES_SIZE, SystemUtils.a(new TreeMap(), (treemap) -> { // Paper - decompile fix
        // Paper end
        treemap.put("doFireTick", new GameRules.GameRuleDefinition("true", GameRules.EnumGameRuleType.BOOLEAN_VALUE));
        treemap.put("mobGriefing", new GameRules.GameRuleDefinition("true", GameRules.EnumGameRuleType.BOOLEAN_VALUE));
        treemap.put("keepInventory", new GameRules.GameRuleDefinition("false", GameRules.EnumGameRuleType.BOOLEAN_VALUE));
        treemap.put("doMobSpawning", new GameRules.GameRuleDefinition("true", GameRules.EnumGameRuleType.BOOLEAN_VALUE));
        treemap.put("doMobLoot", new GameRules.GameRuleDefinition("true", GameRules.EnumGameRuleType.BOOLEAN_VALUE));
        treemap.put("doTileDrops", new GameRules.GameRuleDefinition("true", GameRules.EnumGameRuleType.BOOLEAN_VALUE));
        treemap.put("doEntityDrops", new GameRules.GameRuleDefinition("true", GameRules.EnumGameRuleType.BOOLEAN_VALUE));
        treemap.put("commandBlockOutput", new GameRules.GameRuleDefinition("true", GameRules.EnumGameRuleType.BOOLEAN_VALUE));
        treemap.put("naturalRegeneration", new GameRules.GameRuleDefinition("true", GameRules.EnumGameRuleType.BOOLEAN_VALUE));
        treemap.put("doDaylightCycle", new GameRules.GameRuleDefinition("true", GameRules.EnumGameRuleType.BOOLEAN_VALUE));
        treemap.put("logAdminCommands", new GameRules.GameRuleDefinition("true", GameRules.EnumGameRuleType.BOOLEAN_VALUE));
        treemap.put("showDeathMessages", new GameRules.GameRuleDefinition("true", GameRules.EnumGameRuleType.BOOLEAN_VALUE));
        treemap.put("randomTickSpeed", new GameRules.GameRuleDefinition("3", GameRules.EnumGameRuleType.NUMERICAL_VALUE));
        treemap.put("sendCommandFeedback", new GameRules.GameRuleDefinition("true", GameRules.EnumGameRuleType.BOOLEAN_VALUE));
        treemap.put("reducedDebugInfo", new GameRules.GameRuleDefinition("false", GameRules.EnumGameRuleType.BOOLEAN_VALUE, (minecraftserver, gamerules_gamerulevalue) -> {
            int i = gamerules_gamerulevalue.b() ? 22 : 23;
            Iterator iterator = minecraftserver.getPlayerList().v().iterator();

            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityStatus(entityplayer, (byte) i));
            }

        }));
        treemap.put("spectatorsGenerateChunks", new GameRules.GameRuleDefinition("true", GameRules.EnumGameRuleType.BOOLEAN_VALUE));
        treemap.put("spawnRadius", new GameRules.GameRuleDefinition("10", GameRules.EnumGameRuleType.NUMERICAL_VALUE));
        treemap.put("disableElytraMovementCheck", new GameRules.GameRuleDefinition("false", GameRules.EnumGameRuleType.BOOLEAN_VALUE));
        treemap.put("maxEntityCramming", new GameRules.GameRuleDefinition("24", GameRules.EnumGameRuleType.NUMERICAL_VALUE));
        treemap.put("doWeatherCycle", new GameRules.GameRuleDefinition("true", GameRules.EnumGameRuleType.BOOLEAN_VALUE));
        treemap.put("doLimitedCrafting", new GameRules.GameRuleDefinition("false", GameRules.EnumGameRuleType.BOOLEAN_VALUE));
        treemap.put("maxCommandChainLength", new GameRules.GameRuleDefinition("65536", GameRules.EnumGameRuleType.NUMERICAL_VALUE));
        treemap.put("announceAdvancements", new GameRules.GameRuleDefinition("true", GameRules.EnumGameRuleType.BOOLEAN_VALUE));
    })); // Paper - Optimize GameRules
    private final java.util.LinkedHashMap<String, GameRuleValue> b = new java.util.LinkedHashMap<>(RULES_SIZE); // Paper - Optimize GameRules

    public GameRules() {
        Iterator iterator = GameRules.a.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<String, GameRules.GameRuleDefinition> entry = (Entry) iterator.next();

            this.b.put(entry.getKey(), ((GameRules.GameRuleDefinition) entry.getValue()).a());
        }

    }

    public void set(String s, String s1, @Nullable MinecraftServer minecraftserver) {
        GameRules.GameRuleValue gamerules_gamerulevalue = (GameRules.GameRuleValue) this.b.get(s);

        if (gamerules_gamerulevalue != null) {
            gamerules_gamerulevalue.a(s1, minecraftserver);
        }

    }

    public boolean getBoolean(String s) {
        GameRules.GameRuleValue gamerules_gamerulevalue = (GameRules.GameRuleValue) this.b.get(s);

        return gamerules_gamerulevalue != null ? gamerules_gamerulevalue.b() : false;
    }

    public int c(String s) {
        GameRules.GameRuleValue gamerules_gamerulevalue = (GameRules.GameRuleValue) this.b.get(s);

        return gamerules_gamerulevalue != null ? gamerules_gamerulevalue.c() : 0;
    }

    public NBTTagCompound a() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        Iterator iterator = this.b.keySet().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();
            GameRules.GameRuleValue gamerules_gamerulevalue = (GameRules.GameRuleValue) this.b.get(s);

            nbttagcompound.setString(s, gamerules_gamerulevalue.a());
        }

        return nbttagcompound;
    }

    public void a(NBTTagCompound nbttagcompound) {
        Set<String> set = nbttagcompound.getKeys();
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            this.set(s, nbttagcompound.getString(s), (MinecraftServer) null);
        }

    }

    public GameRules.GameRuleValue get(String s) {
        return (GameRules.GameRuleValue) this.b.get(s);
    }

    public static java.util.LinkedHashMap<String, GameRuleDefinition> getGameRules() { // Paper - Optimize GameRules
        return GameRules.a;
    }

    public static enum EnumGameRuleType {

        ANY_VALUE(StringArgumentType::greedyString, (commandcontext, s) -> {
            return (String) commandcontext.getArgument(s, String.class);
        }), BOOLEAN_VALUE(BoolArgumentType::bool, (commandcontext, s) -> {
            return ((Boolean) commandcontext.getArgument(s, Boolean.class)).toString();
        }), NUMERICAL_VALUE(IntegerArgumentType::integer, (commandcontext, s) -> {
            return ((Integer) commandcontext.getArgument(s, Integer.class)).toString();
        });

        private final Supplier<ArgumentType<?>> d;
        private final BiFunction<CommandContext<CommandListenerWrapper>, String, String> e;

        private EnumGameRuleType(Supplier supplier, BiFunction<CommandContext<CommandListenerWrapper>, String, String> bifunction) { // Paper - decompile fix
            this.d = supplier;
            this.e = bifunction;
        }

        public RequiredArgumentBuilder<CommandListenerWrapper, ?> a(String s) {
            return CommandDispatcher.a(s, (ArgumentType) this.d.get());
        }

        public void a(CommandContext<CommandListenerWrapper> commandcontext, String s, GameRules.GameRuleValue gamerules_gamerulevalue) {
            gamerules_gamerulevalue.a((String) this.e.apply(commandcontext, s), ((CommandListenerWrapper) commandcontext.getSource()).getServer());
        }
    }

    public static class GameRuleValue {

        private String a;
        private boolean b;
        private int c;
        private double d;
        private final GameRules.EnumGameRuleType e;
        private final BiConsumer<MinecraftServer, GameRules.GameRuleValue> f;

        public GameRuleValue(String s, GameRules.EnumGameRuleType gamerules_enumgameruletype, BiConsumer<MinecraftServer, GameRules.GameRuleValue> biconsumer) {
            this.e = gamerules_enumgameruletype;
            this.f = biconsumer;
            this.a(s, (MinecraftServer) null);
        }

        public void a(String s, @Nullable MinecraftServer minecraftserver) {
            this.a = s;
            this.b = Boolean.parseBoolean(s);
            this.c = this.b ? 1 : 0;

            try {
                this.c = Integer.parseInt(s);
            } catch (NumberFormatException numberformatexception) {
                ;
            }

            try {
                this.d = Double.parseDouble(s);
            } catch (NumberFormatException numberformatexception1) {
                ;
            }

            if (minecraftserver != null) {
                this.f.accept(minecraftserver, this);
            }

        }

        public String a() {
            return this.a;
        }

        public boolean b() {
            return this.b;
        }

        public int c() {
            return this.c;
        }

        public GameRules.EnumGameRuleType getType() {
            return this.e;
        }
    }

    public static class GameRuleDefinition {

        private final GameRules.EnumGameRuleType a;
        private final String b;
        private final BiConsumer<MinecraftServer, GameRules.GameRuleValue> c;

        public GameRuleDefinition(String s, GameRules.EnumGameRuleType gamerules_enumgameruletype) {
            this(s, gamerules_enumgameruletype, (minecraftserver, gamerules_gamerulevalue) -> {
            });
        }

        public GameRuleDefinition(String s, GameRules.EnumGameRuleType gamerules_enumgameruletype, BiConsumer<MinecraftServer, GameRules.GameRuleValue> biconsumer) {
            this.a = gamerules_enumgameruletype;
            this.b = s;
            this.c = biconsumer;
        }

        public GameRules.GameRuleValue a() {
            return new GameRules.GameRuleValue(this.b, this.a, this.c);
        }

        public GameRules.EnumGameRuleType b() {
            return this.a;
        }
    }
}
