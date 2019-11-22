package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BehaviorInteractDoor extends Behavior<EntityLiving> {

    public BehaviorInteractDoor() {
        super(ImmutableMap.of(MemoryModuleType.PATH, MemoryStatus.VALUE_PRESENT, MemoryModuleType.INTERACTABLE_DOORS, MemoryStatus.VALUE_PRESENT, MemoryModuleType.OPENED_DOORS, MemoryStatus.REGISTERED));
    }

    @Override
    protected void a(WorldServer worldserver, EntityLiving entityliving, long i) {
        BehaviorController<?> behaviorcontroller = entityliving.getBehaviorController();
        PathEntity pathentity = (PathEntity) behaviorcontroller.getMemory(MemoryModuleType.PATH).get();
        List<GlobalPos> list = (List) behaviorcontroller.getMemory(MemoryModuleType.INTERACTABLE_DOORS).get();
        List<BlockPosition> list1 = (List) pathentity.d().stream().map((pathpoint) -> {
            return new BlockPosition(pathpoint.a, pathpoint.b, pathpoint.c);
        }).collect(Collectors.toList());
        Set<BlockPosition> set = this.a(worldserver, list, list1);
        int j = pathentity.f() - 1;

        this.a(worldserver, list1, set, j, entityliving, behaviorcontroller);
    }

    private Set<BlockPosition> a(WorldServer worldserver, List<GlobalPos> list, List<BlockPosition> list1) {
        Stream stream = list.stream().filter((globalpos) -> {
            return globalpos.getDimensionManager() == worldserver.getWorldProvider().getDimensionManager();
        }).map(GlobalPos::getBlockPosition);

        list1.getClass();
        return (Set) stream.filter(list1::contains).collect(Collectors.toSet());
    }

    private void a(WorldServer worldserver, List<BlockPosition> list, Set<BlockPosition> set, int i, EntityLiving entityliving, BehaviorController<?> behaviorcontroller) {
        set.forEach((blockposition) -> {
            int j = list.indexOf(blockposition);
            IBlockData iblockdata = worldserver.getType(blockposition);
            Block block = iblockdata.getBlock();

            if (TagsBlock.WOODEN_DOORS.isTagged(block) && block instanceof BlockDoor) {
                boolean flag = j >= i;

                // CraftBukkit start - entities opening doors
                org.bukkit.event.entity.EntityInteractEvent event = new org.bukkit.event.entity.EntityInteractEvent(entityliving.getBukkitEntity(), org.bukkit.craftbukkit.block.CraftBlock.at(entityliving.world, blockposition));
                entityliving.world.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    return;
                }
                // CaftBukkit end
                ((BlockDoor) block).setDoor(worldserver, blockposition, flag);
                GlobalPos globalpos = GlobalPos.create(worldserver.getWorldProvider().getDimensionManager(), blockposition);

                if (!behaviorcontroller.getMemory(MemoryModuleType.OPENED_DOORS).isPresent() && flag) {
                    behaviorcontroller.setMemory(MemoryModuleType.OPENED_DOORS, Sets.newHashSet(new GlobalPos[]{globalpos})); // CraftBukkit - decompile error
                } else {
                    behaviorcontroller.getMemory(MemoryModuleType.OPENED_DOORS).ifPresent((set1) -> {
                        if (flag) {
                            set1.add(globalpos);
                        } else {
                            set1.remove(globalpos);
                        }

                    });
                }
            }

        });
        a(worldserver, list, i, entityliving, behaviorcontroller);
    }

    public static void a(WorldServer worldserver, List<BlockPosition> list, int i, EntityLiving entityliving, BehaviorController<?> behaviorcontroller) {
        behaviorcontroller.getMemory(MemoryModuleType.OPENED_DOORS).ifPresent((set) -> {
            Iterator iterator = set.iterator();

            while (iterator.hasNext()) {
                GlobalPos globalpos = (GlobalPos) iterator.next();
                BlockPosition blockposition = globalpos.getBlockPosition();
                int j = list.indexOf(blockposition);

                if (worldserver.getWorldProvider().getDimensionManager() != globalpos.getDimensionManager()) {
                    iterator.remove();
                } else {
                    IBlockData iblockdata = worldserver.getType(blockposition);
                    Block block = iblockdata.getBlock();

                    if (TagsBlock.WOODEN_DOORS.isTagged(block) && block instanceof BlockDoor && j < i && blockposition.a((IPosition) entityliving.getPositionVector(), 4.0D)) {
                        ((BlockDoor) block).setDoor(worldserver, blockposition, false);
                        iterator.remove();
                    }
                }
            }

        });
    }
}
