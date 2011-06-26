package net.minecraft.server;

// CraftBukkit start
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.PigZapEvent;
// CraftBukkit end

public class EntityPig extends EntityAnimal {

    public EntityPig(World world) {
        super(world);
        this.texture = "/mob/pig.png";
        this.b(0.9F, 0.9F);
    }

    protected void b() {
        this.datawatcher.a(16, Byte.valueOf((byte) 0));
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.a("Saddle", this.hasSaddle());
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setSaddle(nbttagcompound.m("Saddle"));
    }

    protected String g() {
        return "mob.pig";
    }

    protected String h() {
        return "mob.pig";
    }

    protected String i() {
        return "mob.pigdeath";
    }

    public boolean a(EntityHuman entityhuman) {
        if (this.hasSaddle() && !this.world.isStatic && (this.passenger == null || this.passenger == entityhuman)) {
            entityhuman.mount(this);
            return true;
        } else {
            return false;
        }
    }

    protected int j() {
        return this.fireTicks > 0 ? Item.GRILLED_PORK.id : Item.PORK.id;
    }

    public boolean hasSaddle() {
        return (this.datawatcher.a(16) & 1) != 0;
    }

    public void setSaddle(boolean flag) {
        if (flag) {
            this.datawatcher.watch(16, Byte.valueOf((byte) 1));
        } else {
            this.datawatcher.watch(16, Byte.valueOf((byte) 0));
        }
    }

    public void a(EntityWeatherStorm entityweatherstorm) {
        if (!this.world.isStatic) {
            EntityPigZombie entitypigzombie = new EntityPigZombie(this.world);

            // CraftBukkit start
            PigZapEvent event = new PigZapEvent(this.getBukkitEntity(), entityweatherstorm.getBukkitEntity(), entitypigzombie.getBukkitEntity());
            this.world.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return;
            }
            // CraftBukkit end

            entitypigzombie.setPositionRotation(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
             // CraftBukkit - added a reason for spawning this creature
            this.world.addEntity(entitypigzombie, SpawnReason.LIGHTNING);
            this.die();
        }
    }

    protected void a(float f) {
        super.a(f);
        if (f > 5.0F && this.passenger instanceof EntityHuman) {
            ((EntityHuman) this.passenger).a((Statistic) AchievementList.u);
        }
    }
}
