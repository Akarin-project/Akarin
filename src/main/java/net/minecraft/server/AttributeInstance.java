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

    void b(AttributeModifier attributemodifier);

    void c(AttributeModifier attributemodifier);

    void b(UUID uuid);

    double getValue();
}
