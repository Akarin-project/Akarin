package net.minecraft.server;

public interface IRangedEntity {

    void a(EntityLiving entityliving, float f); default void rangedAttack(EntityLiving entityliving, float f) { a(entityliving, f); } // Paper - OBFHELPER

    void s(boolean flag); default void setChargingAttack(boolean flag) { s(flag); } // Paper - OBFHELPER
}
