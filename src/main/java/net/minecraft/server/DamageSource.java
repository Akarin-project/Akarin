package net.minecraft.server;

import javax.annotation.Nullable;

public class DamageSource {

    public static final DamageSource FIRE = (new DamageSource("inFire")).setExplosion();
    public static final DamageSource LIGHTNING = new DamageSource("lightningBolt");
    public static final DamageSource BURN = (new DamageSource("onFire")).setIgnoreArmor().setExplosion();
    public static final DamageSource LAVA = (new DamageSource("lava")).setExplosion();
    public static final DamageSource HOT_FLOOR = (new DamageSource("hotFloor")).setExplosion();
    public static final DamageSource STUCK = (new DamageSource("inWall")).setIgnoreArmor();
    public static final DamageSource CRAMMING = (new DamageSource("cramming")).setIgnoreArmor();
    public static final DamageSource DROWN = (new DamageSource("drown")).setIgnoreArmor();
    public static final DamageSource STARVE = (new DamageSource("starve")).setIgnoreArmor().n();
    public static final DamageSource CACTUS = new DamageSource("cactus");
    public static final DamageSource FALL = (new DamageSource("fall")).setIgnoreArmor();
    public static final DamageSource FLY_INTO_WALL = (new DamageSource("flyIntoWall")).setIgnoreArmor();
    public static final DamageSource OUT_OF_WORLD = (new DamageSource("outOfWorld")).setIgnoreArmor().m();
    public static final DamageSource GENERIC = (new DamageSource("generic")).setIgnoreArmor();
    public static final DamageSource MAGIC = (new DamageSource("magic")).setIgnoreArmor().setMagic();
    public static final DamageSource WITHER = (new DamageSource("wither")).setIgnoreArmor();
    public static final DamageSource ANVIL = new DamageSource("anvil");
    public static final DamageSource FALLING_BLOCK = new DamageSource("fallingBlock");
    public static final DamageSource DRAGON_BREATH = (new DamageSource("dragonBreath")).setIgnoreArmor();
    public static final DamageSource FIREWORKS = (new DamageSource("fireworks")).e();
    public static final DamageSource DRYOUT = new DamageSource("dryout");
    public static final DamageSource SWEET_BERRY_BUSH = new DamageSource("sweetBerryBush");
    private boolean x;
    private boolean y;
    private boolean z;
    private float A = 0.1F;
    private boolean B;
    private boolean C;
    private boolean D;
    private boolean E;
    private boolean F;
    public final String translationIndex;
    // CraftBukkit start
    private boolean sweep;

    public boolean isSweep() {
        return sweep;
    }

    public DamageSource sweep() {
        this.sweep = true;
        return this;
    }
    // CraftBukkit end

    public static DamageSource mobAttack(EntityLiving entityliving) {
        return new EntityDamageSource("mob", entityliving);
    }

    public static DamageSource a(Entity entity, EntityLiving entityliving) {
        return new EntityDamageSourceIndirect("mob", entity, entityliving);
    }

    public static DamageSource playerAttack(EntityHuman entityhuman) {
        return new EntityDamageSource("player", entityhuman);
    }

    public static DamageSource arrow(EntityArrow entityarrow, @Nullable Entity entity) {
        return (new EntityDamageSourceIndirect("arrow", entityarrow, entity)).c();
    }

    public static DamageSource a(Entity entity, @Nullable Entity entity1) {
        return (new EntityDamageSourceIndirect("trident", entity, entity1)).c();
    }

    public static DamageSource fireball(EntityFireball entityfireball, @Nullable Entity entity) {
        return entity == null ? (new EntityDamageSourceIndirect("onFire", entityfireball, entityfireball)).setExplosion().c() : (new EntityDamageSourceIndirect("fireball", entityfireball, entity)).setExplosion().c();
    }

    public static DamageSource projectile(Entity entity, @Nullable Entity entity1) {
        return (new EntityDamageSourceIndirect("thrown", entity, entity1)).c();
    }

    public static DamageSource c(Entity entity, @Nullable Entity entity1) {
        return (new EntityDamageSourceIndirect("indirectMagic", entity, entity1)).setIgnoreArmor().setMagic();
    }

    public static DamageSource a(Entity entity) {
        return (new EntityDamageSource("thorns", entity)).x().setMagic();
    }

    public static DamageSource explosion(@Nullable Explosion explosion) {
        return explosion != null && explosion.getSource() != null ? (new EntityDamageSource("explosion.player", explosion.getSource())).r().e() : (new DamageSource("explosion")).r().e();
    }

    public static DamageSource b(@Nullable EntityLiving entityliving) {
        return entityliving != null ? (new EntityDamageSource("explosion.player", entityliving)).r().e() : (new DamageSource("explosion")).r().e();
    }

    public static DamageSource a() {
        return new DamageSourceNetherBed();
    }

    public boolean b() {
        return this.C;
    }

    public DamageSource c() {
        this.C = true;
        return this;
    }

    public boolean isExplosion() {
        return this.F;
    }

    public DamageSource e() {
        this.F = true;
        return this;
    }

    public boolean ignoresArmor() {
        return this.x;
    }

    public float getExhaustionCost() {
        return this.A;
    }

    public boolean ignoresInvulnerability() {
        return this.y;
    }

    public boolean isStarvation() {
        return this.z;
    }

    protected DamageSource(String s) {
        this.translationIndex = s;
    }

    @Nullable
    public Entity j() {
        return this.getEntity();
    }

    @Nullable
    public Entity getEntity() {
        return null;
    }

    protected DamageSource setIgnoreArmor() {
        this.x = true;
        this.A = 0.0F;
        return this;
    }

    protected DamageSource m() {
        this.y = true;
        return this;
    }

    protected DamageSource n() {
        this.z = true;
        this.A = 0.0F;
        return this;
    }

    protected DamageSource setExplosion() {
        this.B = true;
        return this;
    }

    public IChatBaseComponent getLocalizedDeathMessage(EntityLiving entityliving) {
        EntityLiving entityliving1 = entityliving.getKillingEntity();
        String s = "death.attack." + this.translationIndex;
        String s1 = s + ".player";

        return entityliving1 != null ? new ChatMessage(s1, new Object[]{entityliving.getScoreboardDisplayName(), entityliving1.getScoreboardDisplayName()}) : new ChatMessage(s, new Object[]{entityliving.getScoreboardDisplayName()});
    }

    public boolean p() {
        return this.B;
    }

    public String q() {
        return this.translationIndex;
    }

    public DamageSource r() {
        this.D = true;
        return this;
    }

    public boolean s() {
        return this.D;
    }

    public boolean isMagic() {
        return this.E;
    }

    public DamageSource setMagic() {
        this.E = true;
        return this;
    }

    public boolean v() {
        Entity entity = this.getEntity();

        return entity instanceof EntityHuman && ((EntityHuman) entity).abilities.canInstantlyBuild;
    }

    @Nullable
    public Vec3D w() {
        return null;
    }
}
