package net.minecraft.server;

public abstract class EntityMonster extends EntityCreature implements IMonster {

    public org.bukkit.craftbukkit.entity.CraftMonster getBukkitMonster() { return (org.bukkit.craftbukkit.entity.CraftMonster) super.getBukkitEntity(); } // Paper
    protected EntityMonster(EntityTypes<?> entitytypes, World world) {
        super(entitytypes, world);
        this.b_ = 5;
    }

    public SoundCategory getSoundCategory() { return bV(); } // Paper - OBFHELPER
    public SoundCategory bV() {
        return SoundCategory.HOSTILE;
    }

    public void movementTick() {
        this.cy();
        float f = this.az();

        if (f > 0.5F) {
            this.ticksFarFromPlayer += 2;
        }

        super.movementTick();
    }

    public void tick() {
        super.tick();
        if (!this.world.isClientSide && this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
            this.die();
        }

    }

    protected SoundEffect ad() {
        return SoundEffects.ENTITY_HOSTILE_SWIM;
    }

    protected SoundEffect ae() {
        return SoundEffects.ENTITY_HOSTILE_SPLASH;
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        return this.isInvulnerable(damagesource) ? false : super.damageEntity(damagesource, f);
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ENTITY_HOSTILE_HURT;
    }

    protected SoundEffect cs() {
        return SoundEffects.ENTITY_HOSTILE_DEATH;
    }

    protected SoundEffect m(int i) {
        return i > 4 ? SoundEffects.ENTITY_HOSTILE_BIG_FALL : SoundEffects.ENTITY_HOSTILE_SMALL_FALL;
    }

    public float a(BlockPosition blockposition, IWorldReader iworldreader) {
        return 0.5F - iworldreader.A(blockposition);
    }

    protected boolean K_() {
        BlockPosition blockposition = new BlockPosition(this.locX, this.getBoundingBox().minY, this.locZ);

        if (this.world.getBrightness(EnumSkyBlock.SKY, blockposition) > this.random.nextInt(32)) {
            return false;
        } else {
            // Paper start - optimized light check, returns faster
            boolean passes;
            if (this.world.Y()) {
                final int orig = world.getSkylightSubtracted();
                world.setSkylightSubtracted(10);
                passes = !this.world.isLightLevel(blockposition, this.random.nextInt(8));
                world.setSkylightSubtracted(orig);
            } else {
                passes = !this.world.isLightLevel(blockposition, this.random.nextInt(8));
            }
            return passes;
            // Paper end
        }
    }

    public boolean a(GeneratorAccess generatoraccess, boolean flag) {
        return generatoraccess.getDifficulty() != EnumDifficulty.PEACEFUL && this.K_() && super.a(generatoraccess, flag);
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeMap().b(GenericAttributes.ATTACK_DAMAGE);
    }

    protected boolean isDropExperience() {
        return true;
    }

    public boolean c(EntityHuman entityhuman) {
        return true;
    }
}
