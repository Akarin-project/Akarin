package net.minecraft.server;

import java.util.Collection;
import java.util.UUID;
import javax.annotation.Nullable;

public interface AttributeInstance {

    IAttribute getAttribute();

    double b();

    void setValue(double d0);

    Collection<AttributeModifier> a(int i);

    Collection<AttributeModifier> c();

    boolean a(AttributeModifier attributemodifier);

    @Nullable
    AttributeModifier a(UUID uuid);

    default void addModifier(AttributeModifier modifier) { b(modifier); } // Paper - OBFHELPER
    void b(AttributeModifier attributemodifier);

    default void removeModifier(AttributeModifier modifier) { c(modifier); } // Paper - OBFHELPER
    void c(AttributeModifier attributemodifier);

    void b(UUID uuid);

    double getValue();
}
