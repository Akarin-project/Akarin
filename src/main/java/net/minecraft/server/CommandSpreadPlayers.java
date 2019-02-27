package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic4CommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class CommandSpreadPlayers {

    private static final Dynamic4CommandExceptionType a = new Dynamic4CommandExceptionType((object, object1, object2, object3) -> {
        return new ChatMessage("commands.spreadplayers.failed.teams", new Object[] { object, object1, object2, object3});
    });
    private static final Dynamic4CommandExceptionType b = new Dynamic4CommandExceptionType((object, object1, object2, object3) -> {
        return new ChatMessage("commands.spreadplayers.failed.entities", new Object[] { object, object1, object2, object3});
    });

    public static void a(com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> com_mojang_brigadier_commanddispatcher) {
        com_mojang_brigadier_commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandDispatcher.a("spreadplayers").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(CommandDispatcher.a("center", (ArgumentType) ArgumentVec2.a()).then(CommandDispatcher.a("spreadDistance", (ArgumentType) FloatArgumentType.floatArg(0.0F)).then(CommandDispatcher.a("maxRange", (ArgumentType) FloatArgumentType.floatArg(1.0F)).then(CommandDispatcher.a("respectTeams", (ArgumentType) BoolArgumentType.bool()).then(CommandDispatcher.a("targets", (ArgumentType) ArgumentEntity.b()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentVec2.a(commandcontext, "center"), FloatArgumentType.getFloat(commandcontext, "spreadDistance"), FloatArgumentType.getFloat(commandcontext, "maxRange"), BoolArgumentType.getBool(commandcontext, "respectTeams"), ArgumentEntity.b(commandcontext, "targets"));
        })))))));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Vec2F vec2f, float f, float f1, boolean flag, Collection<? extends Entity> collection) throws CommandSyntaxException {
        Random random = new Random();
        double d0 = (double) (vec2f.i - f1);
        double d1 = (double) (vec2f.j - f1);
        double d2 = (double) (vec2f.i + f1);
        double d3 = (double) (vec2f.j + f1);
        CommandSpreadPlayers.a[] acommandspreadplayers_a = a(random, flag ? a(collection) : collection.size(), d0, d1, d2, d3);

        a(vec2f, (double) f, commandlistenerwrapper.getWorld(), random, d0, d1, d2, d3, acommandspreadplayers_a, flag);
        double d4 = a(collection, commandlistenerwrapper.getWorld(), acommandspreadplayers_a, flag);

        commandlistenerwrapper.sendMessage(new ChatMessage("commands.spreadplayers.success." + (flag ? "teams" : "entities"), new Object[] { acommandspreadplayers_a.length, vec2f.i, vec2f.j, String.format(Locale.ROOT, "%.2f", d4)}), true);
        return acommandspreadplayers_a.length;
    }

    private static int a(Collection<? extends Entity> collection) {
        Set<ScoreboardTeamBase> set = Sets.newHashSet();
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            if (entity instanceof EntityHuman) {
                set.add(entity.getScoreboardTeam());
            } else {
                set.add((Object) null);
            }
        }

        return set.size();
    }

    private static void a(Vec2F vec2f, double d0, WorldServer worldserver, Random random, double d1, double d2, double d3, double d4, CommandSpreadPlayers.a[] acommandspreadplayers_a, boolean flag) throws CommandSyntaxException {
        boolean flag1 = true;
        double d5 = 3.4028234663852886E38D;

        int i;

        for (i = 0; i < 10000 && flag1; ++i) {
            flag1 = false;
            d5 = 3.4028234663852886E38D;

            int j;
            CommandSpreadPlayers.a commandspreadplayers_a;

            for (int k = 0; k < acommandspreadplayers_a.length; ++k) {
                CommandSpreadPlayers.a commandspreadplayers_a1 = acommandspreadplayers_a[k];

                j = 0;
                commandspreadplayers_a = new CommandSpreadPlayers.a();

                for (int l = 0; l < acommandspreadplayers_a.length; ++l) {
                    if (k != l) {
                        CommandSpreadPlayers.a commandspreadplayers_a2 = acommandspreadplayers_a[l];
                        double d6 = commandspreadplayers_a1.a(commandspreadplayers_a2);

                        d5 = Math.min(d6, d5);
                        if (d6 < d0) {
                            ++j;
                            commandspreadplayers_a.a = commandspreadplayers_a.a + (commandspreadplayers_a2.a - commandspreadplayers_a1.a);
                            commandspreadplayers_a.b = commandspreadplayers_a.b + (commandspreadplayers_a2.b - commandspreadplayers_a1.b);
                        }
                    }
                }

                if (j > 0) {
                    commandspreadplayers_a.a = commandspreadplayers_a.a / (double) j;
                    commandspreadplayers_a.b = commandspreadplayers_a.b / (double) j;
                    double d7 = (double) commandspreadplayers_a.b();

                    if (d7 > 0.0D) {
                        commandspreadplayers_a.a();
                        commandspreadplayers_a1.b(commandspreadplayers_a);
                    } else {
                        commandspreadplayers_a1.a(random, d1, d2, d3, d4);
                    }

                    flag1 = true;
                }

                if (commandspreadplayers_a1.a(d1, d2, d3, d4)) {
                    flag1 = true;
                }
            }

            if (!flag1) {
                CommandSpreadPlayers.a[] acommandspreadplayers_a1 = acommandspreadplayers_a;
                int i1 = acommandspreadplayers_a.length;

                for (j = 0; j < i1; ++j) {
                    commandspreadplayers_a = acommandspreadplayers_a1[j];
                    if (!commandspreadplayers_a.b((IBlockAccess) worldserver)) {
                        commandspreadplayers_a.a(random, d1, d2, d3, d4);
                        flag1 = true;
                    }
                }
            }
        }

        if (d5 == 3.4028234663852886E38D) {
            d5 = 0.0D;
        }

        if (i >= 10000) {
            if (flag) {
                throw CommandSpreadPlayers.a.create(acommandspreadplayers_a.length, vec2f.i, vec2f.j, String.format(Locale.ROOT, "%.2f", d5));
            } else {
                throw CommandSpreadPlayers.b.create(acommandspreadplayers_a.length, vec2f.i, vec2f.j, String.format(Locale.ROOT, "%.2f", d5));
            }
        }
    }

    private static double a(Collection<? extends Entity> collection, WorldServer worldserver, CommandSpreadPlayers.a[] acommandspreadplayers_a, boolean flag) {
        double d0 = 0.0D;
        int i = 0;
        Map<ScoreboardTeamBase, CommandSpreadPlayers.a> map = Maps.newHashMap();

        double d1;

        for (Iterator iterator = collection.iterator(); iterator.hasNext(); d0 += d1) {
            Entity entity = (Entity) iterator.next();
            CommandSpreadPlayers.a commandspreadplayers_a;

            if (flag) {
                ScoreboardTeamBase scoreboardteambase = entity instanceof EntityHuman ? entity.getScoreboardTeam() : null;

                if (!map.containsKey(scoreboardteambase)) {
                    map.put(scoreboardteambase, acommandspreadplayers_a[i++]);
                }

                commandspreadplayers_a = (CommandSpreadPlayers.a) map.get(scoreboardteambase);
            } else {
                commandspreadplayers_a = acommandspreadplayers_a[i++];
            }

            entity.enderTeleportTo((double) ((float) MathHelper.floor(commandspreadplayers_a.a) + 0.5F), (double) commandspreadplayers_a.a((IBlockAccess) worldserver), (double) MathHelper.floor(commandspreadplayers_a.b) + 0.5D);
            d1 = Double.MAX_VALUE;
            CommandSpreadPlayers.a[] acommandspreadplayers_a1 = acommandspreadplayers_a;
            int j = acommandspreadplayers_a.length;

            for (int k = 0; k < j; ++k) {
                CommandSpreadPlayers.a commandspreadplayers_a1 = acommandspreadplayers_a1[k];

                if (commandspreadplayers_a != commandspreadplayers_a1) {
                    double d2 = commandspreadplayers_a.a(commandspreadplayers_a1);

                    d1 = Math.min(d2, d1);
                }
            }
        }

        if (collection.size() < 2) {
            return 0.0D;
        } else {
            d0 /= (double) collection.size();
            return d0;
        }
    }

    private static CommandSpreadPlayers.a[] a(Random random, int i, double d0, double d1, double d2, double d3) {
        CommandSpreadPlayers.a[] acommandspreadplayers_a = new CommandSpreadPlayers.a[i];

        for (int j = 0; j < acommandspreadplayers_a.length; ++j) {
            CommandSpreadPlayers.a commandspreadplayers_a = new CommandSpreadPlayers.a();

            commandspreadplayers_a.a(random, d0, d1, d2, d3);
            acommandspreadplayers_a[j] = commandspreadplayers_a;
        }

        return acommandspreadplayers_a;
    }

    static class a {

        private double a;
        private double b;

        a() {}

        double a(CommandSpreadPlayers.a commandspreadplayers_a) {
            double d0 = this.a - commandspreadplayers_a.a;
            double d1 = this.b - commandspreadplayers_a.b;

            return Math.sqrt(d0 * d0 + d1 * d1);
        }

        void a() {
            double d0 = (double) this.b();

            this.a /= d0;
            this.b /= d0;
        }

        float b() {
            return MathHelper.sqrt(this.a * this.a + this.b * this.b);
        }

        public void b(CommandSpreadPlayers.a commandspreadplayers_a) {
            this.a -= commandspreadplayers_a.a;
            this.b -= commandspreadplayers_a.b;
        }

        public boolean a(double d0, double d1, double d2, double d3) {
            boolean flag = false;

            if (this.a < d0) {
                this.a = d0;
                flag = true;
            } else if (this.a > d2) {
                this.a = d2;
                flag = true;
            }

            if (this.b < d1) {
                this.b = d1;
                flag = true;
            } else if (this.b > d3) {
                this.b = d3;
                flag = true;
            }

            return flag;
        }

        public int a(IBlockAccess iblockaccess) {
            BlockPosition blockposition = new BlockPosition(this.a, 256.0D, this.b);

            do {
                if (blockposition.getY() <= 0) {
                    return 257;
                }

                blockposition = blockposition.down();
            } while (iblockaccess.getType(blockposition).isAir());

            return blockposition.getY() + 1;
        }

        public boolean b(IBlockAccess iblockaccess) {
            BlockPosition blockposition = new BlockPosition(this.a, 256.0D, this.b);

            IBlockData iblockdata;

            do {
                if (blockposition.getY() <= 0) {
                    return false;
                }

                blockposition = blockposition.down();
                iblockdata = iblockaccess.getType(blockposition);
            } while (iblockdata.isAir());

            Material material = iblockdata.getMaterial();

            return !material.isLiquid() && material != Material.FIRE;
        }

        public void a(Random random, double d0, double d1, double d2, double d3) {
            this.a = MathHelper.a(random, d0, d2);
            this.b = MathHelper.a(random, d1, d3);
        }
    }
}
