package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameRules {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<GameRules.GameRuleKey<?>, GameRules.GameRuleDefinition<?>> z = Maps.newTreeMap(Comparator.comparing((gamerules_gamerulekey) -> {
        return gamerules_gamerulekey.a;
    }));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> DO_FIRE_TICK = a("doFireTick", GameRules.GameRuleBoolean.b(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> MOB_GRIEFING = a("mobGriefing", GameRules.GameRuleBoolean.b(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> KEEP_INVENTORY = a("keepInventory", GameRules.GameRuleBoolean.b(false));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> DO_MOB_SPAWNING = a("doMobSpawning", GameRules.GameRuleBoolean.b(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> DO_MOB_LOOT = a("doMobLoot", GameRules.GameRuleBoolean.b(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> DO_TILE_DROPS = a("doTileDrops", GameRules.GameRuleBoolean.b(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> DO_ENTITY_DROPS = a("doEntityDrops", GameRules.GameRuleBoolean.b(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> COMMAND_BLOCK_OUTPUT = a("commandBlockOutput", GameRules.GameRuleBoolean.b(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> NATURAL_REGENERATION = a("naturalRegeneration", GameRules.GameRuleBoolean.b(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> DO_DAYLIGHT_CYCLE = a("doDaylightCycle", GameRules.GameRuleBoolean.b(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> LOG_ADMIN_COMMANDS = a("logAdminCommands", GameRules.GameRuleBoolean.b(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> SHOW_DEATH_MESSAGES = a("showDeathMessages", GameRules.GameRuleBoolean.b(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleInt> RANDOM_TICK_SPEED = a("randomTickSpeed", GameRules.GameRuleInt.b(3));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> SEND_COMMAND_FEEDBACK = a("sendCommandFeedback", GameRules.GameRuleBoolean.b(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> REDUCED_DEBUG_INFO = a("reducedDebugInfo", GameRules.GameRuleBoolean.b(false, (minecraftserver, gamerules_gameruleboolean) -> {
        int i = gamerules_gameruleboolean.a() ? 22 : 23;
        Iterator iterator = minecraftserver.getPlayerList().getPlayers().iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityStatus(entityplayer, (byte) i));
        }

    }));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> SPECTATORS_GENERATE_CHUNKS = a("spectatorsGenerateChunks", GameRules.GameRuleBoolean.b(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleInt> SPAWN_RADIUS = a("spawnRadius", GameRules.GameRuleInt.b(10));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> DISABLE_ELYTRA_MOVEMENT_CHECK = a("disableElytraMovementCheck", GameRules.GameRuleBoolean.b(false));
    public static final GameRules.GameRuleKey<GameRules.GameRuleInt> MAX_ENTITY_CRAMMING = a("maxEntityCramming", GameRules.GameRuleInt.b(24));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> DO_WEATHER_CYCLE = a("doWeatherCycle", GameRules.GameRuleBoolean.b(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> DO_LIMITED_CRAFTING = a("doLimitedCrafting", GameRules.GameRuleBoolean.b(false));
    public static final GameRules.GameRuleKey<GameRules.GameRuleInt> MAX_COMMAND_CHAIN_LENGTH = a("maxCommandChainLength", GameRules.GameRuleInt.b(65536));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> ANNOUNCE_ADVANCEMENTS = a("announceAdvancements", GameRules.GameRuleBoolean.b(true));
    public static final GameRules.GameRuleKey<GameRules.GameRuleBoolean> x = a("disableRaids", GameRules.GameRuleBoolean.b(false));
    private final Map<GameRules.GameRuleKey<?>, GameRules.GameRuleValue<?>> A;

    private static <T extends GameRules.GameRuleValue<T>> GameRules.GameRuleKey<T> a(String s, GameRules.GameRuleDefinition<T> gamerules_gameruledefinition) {
        GameRules.GameRuleKey<T> gamerules_gamerulekey = new GameRules.GameRuleKey<>(s);
        GameRules.GameRuleDefinition<?> gamerules_gameruledefinition1 = (GameRules.GameRuleDefinition) GameRules.z.put(gamerules_gamerulekey, gamerules_gameruledefinition);

        if (gamerules_gameruledefinition1 != null) {
            throw new IllegalStateException("Duplicate game rule registration for " + s);
        } else {
            return gamerules_gamerulekey;
        }
    }

    public GameRules() {
        this.A = (Map) GameRules.z.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (entry) -> {
            return ((GameRules.GameRuleDefinition) entry.getValue()).getValue();
        }));
    }

    public <T extends GameRules.GameRuleValue<T>> T get(GameRules.GameRuleKey<T> gamerules_gamerulekey) {
        return (T) this.A.get(gamerules_gamerulekey); // CraftBukkit - decompile error
    }

    public NBTTagCompound a() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        this.A.forEach((gamerules_gamerulekey, gamerules_gamerulevalue) -> {
            nbttagcompound.setString(gamerules_gamerulekey.a, gamerules_gamerulevalue.getValue());
        });
        return nbttagcompound;
    }

    public void a(NBTTagCompound nbttagcompound) {
        this.A.forEach((gamerules_gamerulekey, gamerules_gamerulevalue) -> {
            gamerules_gamerulevalue.setValue(nbttagcompound.getString(gamerules_gamerulekey.a));
        });
    }

    public static void a(GameRules.GameRuleVisitor gamerules_gamerulevisitor) {
        GameRules.z.forEach((gamerules_gamerulekey, gamerules_gameruledefinition) -> {
            a(gamerules_gamerulevisitor, gamerules_gamerulekey, gamerules_gameruledefinition);
        });
    }

    private static <T extends GameRules.GameRuleValue<T>> void a(GameRules.GameRuleVisitor gamerules_gamerulevisitor, GameRules.GameRuleKey<?> gamerules_gamerulekey, GameRules.GameRuleDefinition<?> gamerules_gameruledefinition) {
        gamerules_gamerulevisitor.a((GameRules.GameRuleKey<T>) gamerules_gamerulekey, (GameRules.GameRuleDefinition<T>) gamerules_gameruledefinition); // CraftBukkit - decompile error
    }

    public boolean getBoolean(GameRules.GameRuleKey<GameRules.GameRuleBoolean> gamerules_gamerulekey) {
        return ((GameRules.GameRuleBoolean) this.get(gamerules_gamerulekey)).a();
    }

    public int getInt(GameRules.GameRuleKey<GameRules.GameRuleInt> gamerules_gamerulekey) {
        return ((GameRules.GameRuleInt) this.get(gamerules_gamerulekey)).a();
    }

    public static class GameRuleBoolean extends GameRules.GameRuleValue<GameRules.GameRuleBoolean> {

        private boolean a;

        private static GameRules.GameRuleDefinition<GameRules.GameRuleBoolean> b(boolean flag, BiConsumer<MinecraftServer, GameRules.GameRuleBoolean> biconsumer) {
            return new GameRules.GameRuleDefinition<>(BoolArgumentType::bool, (gamerules_gameruledefinition) -> {
                return new GameRules.GameRuleBoolean(gamerules_gameruledefinition, flag);
            }, biconsumer);
        }

        private static GameRules.GameRuleDefinition<GameRules.GameRuleBoolean> b(boolean flag) {
            return b(flag, (minecraftserver, gamerules_gameruleboolean) -> {
            });
        }

        public GameRuleBoolean(GameRules.GameRuleDefinition<GameRules.GameRuleBoolean> gamerules_gameruledefinition, boolean flag) {
            super(gamerules_gameruledefinition);
            this.a = flag;
        }

        @Override
        protected void a(CommandContext<CommandListenerWrapper> commandcontext, String s) {
            this.a = BoolArgumentType.getBool(commandcontext, s);
        }

        public boolean a() {
            return this.a;
        }

        public void a(boolean flag, @Nullable MinecraftServer minecraftserver) {
            this.a = flag;
            this.onChange(minecraftserver);
        }

        @Override
        protected String getValue() {
            return Boolean.toString(this.a);
        }

        @Override
        public void setValue(String s) { // PAIL - private->public
            this.a = Boolean.parseBoolean(s);
        }

        @Override
        public int getIntValue() {
            return this.a ? 1 : 0;
        }

        @Override
        protected GameRules.GameRuleBoolean e() {
            return this;
        }
    }

    public static class GameRuleInt extends GameRules.GameRuleValue<GameRules.GameRuleInt> {

        private int a;

        private static GameRules.GameRuleDefinition<GameRules.GameRuleInt> a(int i, BiConsumer<MinecraftServer, GameRules.GameRuleInt> biconsumer) {
            return new GameRules.GameRuleDefinition<>(IntegerArgumentType::integer, (gamerules_gameruledefinition) -> {
                return new GameRules.GameRuleInt(gamerules_gameruledefinition, i);
            }, biconsumer);
        }

        private static GameRules.GameRuleDefinition<GameRules.GameRuleInt> b(int i) {
            return a(i, (minecraftserver, gamerules_gameruleint) -> {
            });
        }

        public GameRuleInt(GameRules.GameRuleDefinition<GameRules.GameRuleInt> gamerules_gameruledefinition, int i) {
            super(gamerules_gameruledefinition);
            this.a = i;
        }

        @Override
        protected void a(CommandContext<CommandListenerWrapper> commandcontext, String s) {
            this.a = IntegerArgumentType.getInteger(commandcontext, s);
        }

        public int a() {
            return this.a;
        }

        @Override
        protected String getValue() {
            return Integer.toString(this.a);
        }

        @Override
        public void setValue(String s) { // PAIL - private->public
            this.a = b(s);
        }

        private static int b(String s) {
            if (!s.isEmpty()) {
                try {
                    return Integer.parseInt(s);
                } catch (NumberFormatException numberformatexception) {
                    GameRules.LOGGER.warn("Failed to parse integer {}", s);
                }
            }

            return 0;
        }

        @Override
        public int getIntValue() {
            return this.a;
        }

        @Override
        protected GameRules.GameRuleInt e() {
            return this;
        }
    }

    public abstract static class GameRuleValue<T extends GameRules.GameRuleValue<T>> {

        private final GameRules.GameRuleDefinition<T> a;

        public GameRuleValue(GameRules.GameRuleDefinition<T> gamerules_gameruledefinition) {
            this.a = gamerules_gameruledefinition;
        }

        protected abstract void a(CommandContext<CommandListenerWrapper> commandcontext, String s);

        public void b(CommandContext<CommandListenerWrapper> commandcontext, String s) {
            this.a(commandcontext, s);
            this.onChange(((CommandListenerWrapper) commandcontext.getSource()).getServer());
        }

        public void onChange(@Nullable MinecraftServer minecraftserver) {
            if (minecraftserver != null) {
                this.a.c.accept(minecraftserver, this.e());
            }

        }

        public abstract void setValue(String s); // PAIL - private->public

        protected abstract String getValue();

        public String toString() {
            return this.getValue();
        }

        public abstract int getIntValue();

        protected abstract T e();
    }

    public static class GameRuleDefinition<T extends GameRules.GameRuleValue<T>> {

        private final Supplier<ArgumentType<?>> a;
        private final Function<GameRules.GameRuleDefinition<T>, T> b;
        private final BiConsumer<MinecraftServer, T> c;

        private GameRuleDefinition(Supplier<ArgumentType<?>> supplier, Function<GameRules.GameRuleDefinition<T>, T> function, BiConsumer<MinecraftServer, T> biconsumer) {
            this.a = supplier;
            this.b = function;
            this.c = biconsumer;
        }

        public RequiredArgumentBuilder<CommandListenerWrapper, ?> a(String s) {
            return CommandDispatcher.a(s, (ArgumentType) this.a.get());
        }

        public T getValue() {
            return this.b.apply(this); // CraftBukkit - decompile error
        }
    }

    public static final class GameRuleKey<T extends GameRules.GameRuleValue<T>> {

        private final String a;

        public GameRuleKey(String s) {
            this.a = s;
        }

        public String toString() {
            return this.a;
        }

        public boolean equals(Object object) {
            return this == object ? true : object instanceof GameRules.GameRuleKey && ((GameRules.GameRuleKey) object).a.equals(this.a);
        }

        public int hashCode() {
            return this.a.hashCode();
        }

        public String a() {
            return this.a;
        }
    }

    @FunctionalInterface
    public interface GameRuleVisitor {

        <T extends GameRules.GameRuleValue<T>> void a(GameRules.GameRuleKey<T> gamerules_gamerulekey, GameRules.GameRuleDefinition<T> gamerules_gameruledefinition);
    }
}
