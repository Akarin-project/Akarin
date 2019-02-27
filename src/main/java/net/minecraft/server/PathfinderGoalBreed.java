package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class PathfinderGoalBreed extends PathfinderGoal {

    protected final EntityAnimal animal;
    private final Class<? extends EntityAnimal> d;
    protected World b;
    protected EntityAnimal partner;
    private int e;
    private final double f;

    public PathfinderGoalBreed(EntityAnimal entityanimal, double d0) {
        this(entityanimal, d0, entityanimal.getClass());
    }

    public PathfinderGoalBreed(EntityAnimal entityanimal, double d0, Class<? extends EntityAnimal> oclass) {
        this.animal = entityanimal;
        this.b = entityanimal.world;
        this.d = oclass;
        this.f = d0;
        this.a(3);
    }

    public boolean a() {
        if (!this.animal.isInLove()) {
            return false;
        } else {
            this.partner = this.i();
            return this.partner != null;
        }
    }

    public boolean b() {
        return this.partner.isAlive() && this.partner.isInLove() && this.e < 60;
    }

    public void d() {
        this.partner = null;
        this.e = 0;
    }

    public void e() {
        this.animal.getControllerLook().a(this.partner, 10.0F, (float) this.animal.K());
        this.animal.getNavigation().a((Entity) this.partner, this.f);
        ++this.e;
        if (this.e >= 60 && this.animal.h(this.partner) < 9.0D) {
            this.g();
        }

    }

    private EntityAnimal i() {
        List<EntityAnimal> list = this.b.a(this.d, this.animal.getBoundingBox().g(8.0D));
        double d0 = Double.MAX_VALUE;
        EntityAnimal entityanimal = null;
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            EntityAnimal entityanimal1 = (EntityAnimal) iterator.next();

            if (this.animal.mate(entityanimal1) && this.animal.h(entityanimal1) < d0) {
                entityanimal = entityanimal1;
                d0 = this.animal.h(entityanimal1);
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
            Random random = this.animal.getRandom();

            for (int i = 0; i < 7; ++i) {
                double d0 = random.nextGaussian() * 0.02D;
                double d1 = random.nextGaussian() * 0.02D;
                double d2 = random.nextGaussian() * 0.02D;
                double d3 = random.nextDouble() * (double) this.animal.width * 2.0D - (double) this.animal.width;
                double d4 = 0.5D + random.nextDouble() * (double) this.animal.length;
                double d5 = random.nextDouble() * (double) this.animal.width * 2.0D - (double) this.animal.width;

                this.b.addParticle(Particles.A, this.animal.locX + d3, this.animal.locY + d4, this.animal.locZ + d5, d0, d1, d2);
            }

            if (this.b.getGameRules().getBoolean("doMobLoot")) {
                // CraftBukkit start - use event experience
                if (experience > 0) {
                    this.b.addEntity(new EntityExperienceOrb(this.b, this.animal.locX, this.animal.locY, this.animal.locZ, experience));
                }
                // CraftBukkit end
            }

        }
    }
}
