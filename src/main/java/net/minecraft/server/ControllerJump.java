package net.minecraft.server;

public class ControllerJump {

    private final EntityInsentient b;
    protected boolean a;

    public ControllerJump(EntityInsentient entityinsentient) {
        this.b = entityinsentient;
    }

    public void a() {
        this.a = true;
    }

    public void b() {
        this.b.o(this.a);
        this.a = false;
    }
}
