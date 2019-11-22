package net.minecraft.server;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class BehaviorUtil {

    public static void a(EntityLiving entityliving, EntityLiving entityliving1) {
        b(entityliving, entityliving1);
        d(entityliving, entityliving1);
    }

    public static boolean a(BehaviorController<?> behaviorcontroller, EntityLiving entityliving) {
        return behaviorcontroller.getMemory(MemoryModuleType.VISIBLE_MOBS).filter((list) -> {
            return list.contains(entityliving);
        }).isPresent();
    }

    public static boolean a(BehaviorController<?> behaviorcontroller, MemoryModuleType<? extends EntityLiving> memorymoduletype, EntityTypes<?> entitytypes) {
        return behaviorcontroller.getMemory(memorymoduletype).filter((entityliving) -> {
            return entityliving.getEntityType() == entitytypes;
        }).filter(EntityLiving::isAlive).filter((entityliving) -> {
            return a(behaviorcontroller, entityliving);
        }).isPresent();
    }

    public static void b(EntityLiving entityliving, EntityLiving entityliving1) {
        c(entityliving, entityliving1);
        c(entityliving1, entityliving);
    }

    public static void c(EntityLiving entityliving, EntityLiving entityliving1) {
        entityliving.getBehaviorController().setMemory(MemoryModuleType.LOOK_TARGET, (new BehaviorPositionEntity(entityliving1))); // CraftBukkit - decompile error
    }

    public static void d(EntityLiving entityliving, EntityLiving entityliving1) {
        boolean flag = true;

        a(entityliving, entityliving1, 2);
        a(entityliving1, entityliving, 2);
    }

    public static void a(EntityLiving entityliving, EntityLiving entityliving1, int i) {
        float f = (float) entityliving.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue();
        BehaviorPositionEntity behaviorpositionentity = new BehaviorPositionEntity(entityliving1);
        MemoryTarget memorytarget = new MemoryTarget(behaviorpositionentity, f, i);

        entityliving.getBehaviorController().setMemory(MemoryModuleType.LOOK_TARGET, behaviorpositionentity); // CraftBukkit - decompile error
        entityliving.getBehaviorController().setMemory(MemoryModuleType.WALK_TARGET, memorytarget); // CraftBukkit - decompile error
    }

    public static void a(EntityLiving entityliving, ItemStack itemstack, EntityLiving entityliving1) {
        if (itemstack.isEmpty()) return; // CraftBukkit - SPIGOT-4940: no empty loot
        double d0 = entityliving.locY - 0.30000001192092896D + (double) entityliving.getHeadHeight();
        EntityItem entityitem = new EntityItem(entityliving.world, entityliving.locX, d0, entityliving.locZ, itemstack);
        BlockPosition blockposition = new BlockPosition(entityliving1);
        BlockPosition blockposition1 = new BlockPosition(entityliving);
        float f = 0.3F;
        Vec3D vec3d = new Vec3D(blockposition.b(blockposition1));

        vec3d = vec3d.d().a(0.30000001192092896D);
        entityitem.setMot(vec3d);
        entityitem.defaultPickupDelay();
        entityliving.world.addEntity(entityitem);
    }

    public static SectionPosition a(WorldServer worldserver, SectionPosition sectionposition, int i) {
        int j = worldserver.b(sectionposition);
        Stream<SectionPosition> stream = SectionPosition.a(sectionposition, i).filter((sectionposition1) -> { // CraftBukkit - decompile error
            return worldserver.b(sectionposition1) < j;
        });

        worldserver.getClass();
        return (SectionPosition) stream.min(Comparator.comparingInt(worldserver::b)).orElse(sectionposition);
    }
}
