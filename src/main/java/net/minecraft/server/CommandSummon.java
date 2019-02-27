package net.minecraft.server;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class CommandSummon {

    private static final SimpleCommandExceptionType a = new SimpleCommandExceptionType(new ChatMessage("commands.summon.failed", new Object[0]));

    public static void a(com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> com_mojang_brigadier_commanddispatcher) {
        com_mojang_brigadier_commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandDispatcher.a("summon").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(((RequiredArgumentBuilder) CommandDispatcher.a("entity", (ArgumentType) ArgumentEntitySummon.a()).suggests(CompletionProviders.d).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntitySummon.a(commandcontext, "entity"), ((CommandListenerWrapper) commandcontext.getSource()).getPosition(), new NBTTagCompound(), true);
        })).then(((RequiredArgumentBuilder) CommandDispatcher.a("pos", (ArgumentType) ArgumentVec3.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntitySummon.a(commandcontext, "entity"), ArgumentVec3.a(commandcontext, "pos"), new NBTTagCompound(), true);
        })).then(CommandDispatcher.a("nbt", (ArgumentType) ArgumentNBTTag.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntitySummon.a(commandcontext, "entity"), ArgumentVec3.a(commandcontext, "pos"), ArgumentNBTTag.a(commandcontext, "nbt"), false);
        })))));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, MinecraftKey minecraftkey, Vec3D vec3d, NBTTagCompound nbttagcompound, boolean flag) throws CommandSyntaxException {
        NBTTagCompound nbttagcompound1 = nbttagcompound.clone();

        nbttagcompound1.setString("id", minecraftkey.toString());
        if (EntityTypes.getName(EntityTypes.LIGHTNING_BOLT).equals(minecraftkey)) {
            EntityLightning entitylightning = new EntityLightning(commandlistenerwrapper.getWorld(), vec3d.x, vec3d.y, vec3d.z, false);

            commandlistenerwrapper.getWorld().strikeLightning(entitylightning);
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.summon.success", new Object[] { entitylightning.getScoreboardDisplayName()}), true);
            return 1;
        } else {
            Entity entity = ChunkRegionLoader.a(nbttagcompound1, commandlistenerwrapper.getWorld(), vec3d.x, vec3d.y, vec3d.z, true);

            if (entity == null) {
                throw CommandSummon.a.create();
            } else {
                entity.setPositionRotation(vec3d.x, vec3d.y, vec3d.z, entity.yaw, entity.pitch);
                if (flag && entity instanceof EntityInsentient) {
                    ((EntityInsentient) entity).prepare(commandlistenerwrapper.getWorld().getDamageScaler(new BlockPosition(entity)), (GroupDataEntity) null, (NBTTagCompound) null);
                }

                commandlistenerwrapper.sendMessage(new ChatMessage("commands.summon.success", new Object[] { entity.getScoreboardDisplayName()}), true);
                return 1;
            }
        }
    }
}
