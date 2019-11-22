package net.minecraft.server;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class PathfinderGoalBreed extends PathfinderGoal {

    private static final PathfinderTargetCondition d = (new PathfinderTargetCondition()).a(8.0D).a().b().c();
    protected final EntityAnimal animal;
    private final Class<? extends EntityAnimal> e;
    protected final World b;
    protected EntityAnimal partner;
    private int f;
    private final double g;

    public PathfinderGoalBreed(EntityAnimal entityanimal, double d0) {
        this(entityanimal, d0, entityanimal.getClass());
    }

    public PathfinderGoalBreed(EntityAnimal entityanimal, double d0, Class<? extends EntityAnimal> oclass) {
        this.animal = entityanimal;
        this.b = entityanimal.world;
        this.e = oclass;
        this.g = d0;
        this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
    }

    @Override
    public boolean a() {
        if (!this.animal.isInLove()) {
            return false;
        } else {
            this.partner = this.h();
            return this.partner != null;
        }
    }

    @Override
    public boolean b() {
        return this.partner.isAlive() && this.partner.isInLove() && this.f < 60;
    }

    @Override
    public void d() {
        this.partner = null;
        this.f = 0;
    }

    @Override
    public void e() {
        this.animal.getControllerLook().a(this.partner, 10.0F, (float) this.animal.M());
        this.animal.getNavigation().a((Entity) this.partner, this.g);
        ++this.f;
        if (this.f >= 60 && this.animal.h((Entity) this.partner) < 9.0D) {
            this.g();
        }

    }

    @Nullable
    private EntityAnimal h() {
        List<EntityAnimal> list = this.b.a(this.e, PathfinderGoalBreed.d, this.animal, this.animal.getBoundingBox().g(8.0D));
        double d0 = Double.MAX_VALUE;
        EntityAnimal entityanimal = null;
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            EntityAnimal entityanimal1 = (EntityAnimal) iterator.next();

            if (this.animal.mate(entityanimal1) && this.animal.h((Entity) entityanimal1) < d0) {
                entityanimal = entityanimal1;
                d0 = this.animal.h((Entity) entityanimal1);
            }
        }

        return entityanimal;
    }

    protected void g() {
        EntityAgeable entityageable = this.animal.createChild(this.partner);

        if (entityageable != null) {
            // CraftBukkit start - set persistence for tame animals
            if (entityageable instanceof EntityTameableAnimal && ((EntityTameableAnimal) entityageable).isTamed()) {
                entityageable.persistent = true;
            }
            // CraftBukkit end
            EntityPlayer entityplayer = this.animal.getBreedCause();

            if (entityplayer == null && this.partner.getBreedCause() != null) {
                entityplayer = this.partner.getBreedCause();
            }
            // CraftBukkit start - call EntityBreedEvent
            int experience = this.animal.getRandom().nextInt(7) + 1;
            org.bukkit.event.entity.EntityBreedEvent entityBreedEvent = org.bukkit.craftbukkit.event.CraftEventFactory.callEntityBreedEvent(entityageable, animal, partner, entityplayer, this.animal.breedItem, experience);
            if (entityBreedEvent.isCancelled()) {
                return;
            }
            experience = entityBreedEvent.getExperience();
            // CraftBukkit end

            if (entityplayer != null) {
                entityplayer.a(StatisticList.ANIMALS_BRED);
                CriterionTriggers.o.a(entityplayer, this.animal, this.partner, entityageable);
            }

            this.animal.setAgeRaw(6000);
            this.partner.setAgeRaw(6000);
            this.animal.resetLove();
            this.partner.resetLove();
            entityageable.setAgeRaw(-24000);
            entityageable.setPositionRotation(this.animal.locX, this.animal.locY, this.animal.locZ, 0.0F, 0.0F);
            this.b.addEntity(entityageable, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.BREEDING); // CraftBukkit - added SpawnReason
            this.b.broadcastEntityEffect(this.animal, (byte) 18);
            if (this.b.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                // CraftBukkit start - use event experience
                if (experience > 0) {
                    this.b.addEntity(new EntityExperienceOrb(this.b, this.animal.locX, this.animal.locY, this.animal.locZ, experience, org.bukkit.entity.ExperienceOrb.SpawnReason.BREED, entityplayer, entityageable)); // Paper
                }
                // CraftBukkit end
            }

        }
    }
}
