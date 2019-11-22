package net.minecraft.server;

import org.bukkit.craftbukkit.event.CraftEventFactory;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class EntityLeash extends EntityHanging {

    public EntityLeash(EntityTypes<? extends EntityLeash> entitytypes, World world) {
        super(entitytypes, world);
    }

    public EntityLeash(World world, BlockPosition blockposition) {
        super(EntityTypes.LEASH_KNOT, world, blockposition);
        this.setPosition((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D);
        float f = 0.125F;
        float f1 = 0.1875F;
        float f2 = 0.25F;

        this.a(new AxisAlignedBB(this.locX - 0.1875D, this.locY - 0.25D + 0.125D, this.locZ - 0.1875D, this.locX + 0.1875D, this.locY + 0.25D + 0.125D, this.locZ + 0.1875D));
        this.attachedToPlayer = true;
    }

    @Override
    public void setPosition(double d0, double d1, double d2) {
        super.setPosition((double) MathHelper.floor(d0) + 0.5D, (double) MathHelper.floor(d1) + 0.5D, (double) MathHelper.floor(d2) + 0.5D);
    }

    @Override
    protected void updateBoundingBox() {
        this.locX = (double) this.blockPosition.getX() + 0.5D;
        this.locY = (double) this.blockPosition.getY() + 0.5D;
        this.locZ = (double) this.blockPosition.getZ() + 0.5D;
        if (valid) ((WorldServer) world).chunkCheck(this); // CraftBukkit
    }

    @Override
    public void setDirection(EnumDirection enumdirection) {}

    @Override
    public int getHangingWidth() {
        return 9;
    }

    @Override
    public int getHangingHeight() {
        return 9;
    }

    @Override
    protected float getHeadHeight(EntityPose entitypose, EntitySize entitysize) {
        return -0.0625F;
    }

    @Override
    public void a(@Nullable Entity entity) {
        this.a(SoundEffects.ENTITY_LEASH_KNOT_BREAK, 1.0F, 1.0F);
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {}

    @Override
    public void a(NBTTagCompound nbttagcompound) {}

    @Override
    public boolean b(EntityHuman entityhuman, EnumHand enumhand) {
        if (this.world.isClientSide) {
            return true;
        } else {
            boolean flag = false;
            double d0 = 7.0D;
            List<EntityInsentient> list = this.world.a(EntityInsentient.class, new AxisAlignedBB(this.locX - 7.0D, this.locY - 7.0D, this.locZ - 7.0D, this.locX + 7.0D, this.locY + 7.0D, this.locZ + 7.0D));
            Iterator iterator = list.iterator();

            EntityInsentient entityinsentient;

            while (iterator.hasNext()) {
                entityinsentient = (EntityInsentient) iterator.next();
                if (entityinsentient.getLeashHolder() == entityhuman) {
                    // CraftBukkit start
                    if (CraftEventFactory.callPlayerLeashEntityEvent(entityinsentient, this, entityhuman).isCancelled()) {
                        ((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutAttachEntity(entityinsentient, entityinsentient.getLeashHolder()));
                        continue;
                    }
                    // CraftBukkit end
                    entityinsentient.setLeashHolder(this, true);
                    flag = true;
                }
            }

            if (!flag) {
                // CraftBukkit start - Move below
                // this.die();
                boolean die = true;
                // CraftBukkit end
                if (true || entityhuman.abilities.canInstantlyBuild) { // CraftBukkit - Process for non-creative as well
                    iterator = list.iterator();

                    while (iterator.hasNext()) {
                        entityinsentient = (EntityInsentient) iterator.next();
                        if (entityinsentient.isLeashed() && entityinsentient.getLeashHolder() == this) {
                            // CraftBukkit start
                            if (CraftEventFactory.callPlayerUnleashEntityEvent(entityinsentient, entityhuman).isCancelled()) {
                                die = false;
                                continue;
                            }
                            entityinsentient.unleash(true, !entityhuman.abilities.canInstantlyBuild); // false -> survival mode boolean
                            // CraftBukkit end
                        }
                    }
                    // CraftBukkit start
                    if (die) {
                        this.die();
                    }
                    // CraftBukkit end
                }
            }

            return true;
        }
    }

    @Override
    public boolean survives() {
        return this.world.getType(this.blockPosition).getBlock().a(TagsBlock.FENCES);
    }

    public static EntityLeash a(World world, BlockPosition blockposition) {
        int i = blockposition.getX();
        int j = blockposition.getY();
        int k = blockposition.getZ();
        List<EntityLeash> list = world.a(EntityLeash.class, new AxisAlignedBB((double) i - 1.0D, (double) j - 1.0D, (double) k - 1.0D, (double) i + 1.0D, (double) j + 1.0D, (double) k + 1.0D));
        Iterator iterator = list.iterator();

        EntityLeash entityleash;

        do {
            if (!iterator.hasNext()) {
                EntityLeash entityleash1 = new EntityLeash(world, blockposition);

                world.addEntity(entityleash1);
                entityleash1.playPlaceSound();
                return entityleash1;
            }

            entityleash = (EntityLeash) iterator.next();
        } while (!entityleash.getBlockPosition().equals(blockposition));

        return entityleash;
    }

    @Override
    public void playPlaceSound() {
        this.a(SoundEffects.ENTITY_LEASH_KNOT_PLACE, 1.0F, 1.0F);
    }

    @Override
    public Packet<?> N() {
        return new PacketPlayOutSpawnEntity(this, this.getEntityType(), 0, this.getBlockPosition());
    }
}
