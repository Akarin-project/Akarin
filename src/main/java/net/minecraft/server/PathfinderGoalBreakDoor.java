package net.minecraft.server;

public class PathfinderGoalBreakDoor extends PathfinderGoalDoorInteract {

    private int d;
    private int e = -1;

    public PathfinderGoalBreakDoor(EntityInsentient entityinsentient) {
        super(entityinsentient);
    }

    public boolean a() {
        return !super.a() ? false : (!this.a.world.getGameRules().getBoolean("mobGriefing") ? false : !this.g());
    }

    public void c() {
        super.c();
        this.d = 0;
    }

    public boolean b() {
        double d0 = this.a.c(this.b);

        return this.d <= 240 && !this.g() && d0 < 4.0D;
    }

    public void d() {
        super.d();
        this.a.world.c(this.a.getId(), this.b, -1);
    }

    public void e() {
        super.e();
        if (this.a.getRandom().nextInt(20) == 0) {
            this.a.world.triggerEffect(1019, this.b, 0);
        }

        ++this.d;
        int i = (int) ((float) this.d / 240.0F * 10.0F);

        if (i != this.e) {
            this.a.world.c(this.a.getId(), this.b, i);
            this.e = i;
        }

        if (this.d == 240 && this.a.world.getDifficulty() == EnumDifficulty.HARD) {
            // CraftBukkit start
            if (org.bukkit.craftbukkit.event.CraftEventFactory.callEntityBreakDoorEvent(this.a, this.b.getX(), this.b.getY(), this.b.getZ()).isCancelled()) {
                this.c();
                return;
            }
            // CraftBukkit end
            this.a.world.setAir(this.b);
            this.a.world.triggerEffect(1021, this.b, 0);
            this.a.world.triggerEffect(2001, this.b, Block.getCombinedId(this.a.world.getType(this.b)));
        }

    }
}
