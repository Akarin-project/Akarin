package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
// CraftBukkit start
import org.bukkit.craftbukkit.entity.CraftVillager;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.VillagerCareerChangeEvent;
// CraftBukkit end

public class BehaviorCareer extends Behavior<EntityVillager> {

    public BehaviorCareer() {
        super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT));
    }

    protected boolean a(WorldServer worldserver, EntityVillager entityvillager) {
        return entityvillager.getVillagerData().getProfession() == VillagerProfession.NONE;
    }

    protected void a(WorldServer worldserver, EntityVillager entityvillager, long i) {
        GlobalPos globalpos = (GlobalPos) entityvillager.getBehaviorController().getMemory(MemoryModuleType.JOB_SITE).get();
        MinecraftServer minecraftserver = worldserver.getMinecraftServer();

        minecraftserver.getWorldServer(globalpos.getDimensionManager()).B().c(globalpos.getBlockPosition()).ifPresent((villageplacetype) -> {
            IRegistry.VILLAGER_PROFESSION.d().filter((villagerprofession) -> {
                return villagerprofession.b() == villageplacetype;
            }).findFirst().ifPresent((villagerprofession) -> {
                // CraftBukkit start - Fire VillagerCareerChangeEvent where Villager gets employed
                VillagerCareerChangeEvent event = CraftEventFactory.callVillagerCareerChangeEvent(entityvillager, CraftVillager.nmsToBukkitProfession(villagerprofession), VillagerCareerChangeEvent.ChangeReason.EMPLOYED);
                if (event.isCancelled()) {
                    return;
                }

                entityvillager.setVillagerData(entityvillager.getVillagerData().withProfession(CraftVillager.bukkitToNmsProfession(event.getProfession())));
                // CraftBukkit end
                entityvillager.a(worldserver);
            });
        });
    }
}
