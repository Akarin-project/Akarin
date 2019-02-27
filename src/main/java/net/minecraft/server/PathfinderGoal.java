package net.minecraft.server;

public abstract class PathfinderGoal {

    private int a;

    public PathfinderGoal() {}

    public abstract boolean a();

    public boolean b() {
        return this.a();
    }

    public boolean f() {
        return true;
    }

    public void c() {}

    public void d() {
        onTaskReset(); // Paper
    }
    public void onTaskReset() {} // Paper

    public void e() {}

    public void a(int i) {
        this.a = i;
    }

    public int h() {
        return this.a;
    }
}
