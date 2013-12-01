package net.minecraft.server;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GenericAttributes {

    private static final Logger k = LogManager.getLogger();
    public static final IAttribute maxHealth = (new AttributeRanged((IAttribute) null, "generic.maxHealth", 20.0D, 0.0D, 1024.0D)).a("Max Health").a(true);
    public static final IAttribute FOLLOW_RANGE = (new AttributeRanged((IAttribute) null, "generic.followRange", 32.0D, 0.0D, 2048.0D)).a("Follow Range");
    public static final IAttribute c = (new AttributeRanged((IAttribute) null, "generic.knockbackResistance", 0.0D, 0.0D, 1.0D)).a("Knockback Resistance");
    public static final IAttribute MOVEMENT_SPEED = (new AttributeRanged((IAttribute) null, "generic.movementSpeed", 0.699999988079071D, 0.0D, 1024.0D)).a("Movement Speed").a(true);
    public static final IAttribute e = (new AttributeRanged((IAttribute) null, "generic.flyingSpeed", 0.4000000059604645D, 0.0D, 1024.0D)).a("Flying Speed").a(true);
    public static final IAttribute ATTACK_DAMAGE = new AttributeRanged((IAttribute) null, "generic.attackDamage", 2.0D, 0.0D, 2048.0D);
    public static final IAttribute g = (new AttributeRanged((IAttribute) null, "generic.attackSpeed", 4.0D, 0.0D, 1024.0D)).a(true);
    public static final IAttribute h = (new AttributeRanged((IAttribute) null, "generic.armor", 0.0D, 0.0D, 30.0D)).a(true);
    public static final IAttribute i = (new AttributeRanged((IAttribute) null, "generic.armorToughness", 0.0D, 0.0D, 20.0D)).a(true);
    public static final IAttribute j = (new AttributeRanged((IAttribute) null, "generic.luck", 0.0D, -1024.0D, 1024.0D)).a(true);

    public static NBTTagList a(AttributeMapBase attributemapbase) {
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = attributemapbase.a().iterator();

        while (iterator.hasNext()) {
            AttributeInstance attributeinstance = (AttributeInstance) iterator.next();

            nbttaglist.add((NBTBase) a(attributeinstance));
        }

        return nbttaglist;
    }

    private static NBTTagCompound a(AttributeInstance attributeinstance) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        IAttribute iattribute = attributeinstance.getAttribute();

        nbttagcompound.setString("Name", iattribute.getName());
        nbttagcompound.setDouble("Base", attributeinstance.b());
        Collection<AttributeModifier> collection = attributeinstance.c();

        if (collection != null && !collection.isEmpty()) {
            NBTTagList nbttaglist = new NBTTagList();
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                AttributeModifier attributemodifier = (AttributeModifier) iterator.next();

                if (attributemodifier.e()) {
                    nbttaglist.add((NBTBase) a(attributemodifier));
                }
            }

            nbttagcompound.set("Modifiers", nbttaglist);
        }

        return nbttagcompound;
    }

    public static NBTTagCompound a(AttributeModifier attributemodifier) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.setString("Name", attributemodifier.b());
        nbttagcompound.setDouble("Amount", attributemodifier.d());
        nbttagcompound.setInt("Operation", attributemodifier.c());
        nbttagcompound.a("UUID", attributemodifier.a());
        return nbttagcompound;
    }

    public static void a(AttributeMapBase attributemapbase, NBTTagList nbttaglist) {
        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
            AttributeInstance attributeinstance = attributemapbase.a(nbttagcompound.getString("Name"));

            if (attributeinstance == null) {
                GenericAttributes.k.warn("Ignoring unknown attribute '{}'", nbttagcompound.getString("Name"));
            } else {
                a(attributeinstance, nbttagcompound);
            }
        }

    }

    private static void a(AttributeInstance attributeinstance, NBTTagCompound nbttagcompound) {
        attributeinstance.setValue(nbttagcompound.getDouble("Base"));
        if (nbttagcompound.hasKeyOfType("Modifiers", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("Modifiers", 10);

            for (int i = 0; i < nbttaglist.size(); ++i) {
                AttributeModifier attributemodifier = a(nbttaglist.getCompound(i));

                if (attributemodifier != null) {
                    AttributeModifier attributemodifier1 = attributeinstance.a(attributemodifier.a());

                    if (attributemodifier1 != null) {
                        attributeinstance.c(attributemodifier1);
                    }

                    attributeinstance.b(attributemodifier);
                }
            }
        }

    }

    @Nullable
    public static AttributeModifier a(NBTTagCompound nbttagcompound) {
        UUID uuid = nbttagcompound.a("UUID");

        try {
            return new AttributeModifier(uuid, nbttagcompound.getString("Name"), nbttagcompound.getDouble("Amount"), nbttagcompound.getInt("Operation"));
        } catch (Exception exception) {
            GenericAttributes.k.warn("Unable to create attribute: {}", exception.getMessage());
            return null;
        }
    }
}
