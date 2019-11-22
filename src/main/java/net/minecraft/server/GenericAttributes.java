package net.minecraft.server;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GenericAttributes {

    private static final Logger LOGGER = LogManager.getLogger();
    // Spigot start
    public static final IAttribute MAX_HEALTH = (new AttributeRanged((IAttribute) null, "generic.maxHealth", 20.0D, 0.0D, org.spigotmc.SpigotConfig.maxHealth)).a("Max Health").a(true);
    public static final IAttribute FOLLOW_RANGE = (new AttributeRanged((IAttribute) null, "generic.followRange", 32.0D, 0.0D, 2048.0D)).a("Follow Range");
    public static final IAttribute KNOCKBACK_RESISTANCE = (new AttributeRanged((IAttribute) null, "generic.knockbackResistance", 0.0D, 0.0D, 1.0D)).a("Knockback Resistance");
    public static final IAttribute MOVEMENT_SPEED = (new AttributeRanged((IAttribute) null, "generic.movementSpeed", 0.699999988079071D, 0.0D, org.spigotmc.SpigotConfig.movementSpeed)).a("Movement Speed").a(true);
    public static final IAttribute FLYING_SPEED = (new AttributeRanged((IAttribute) null, "generic.flyingSpeed", 0.4000000059604645D, 0.0D, 1024.0D)).a("Flying Speed").a(true);
    public static final IAttribute ATTACK_DAMAGE = new AttributeRanged((IAttribute) null, "generic.attackDamage", 2.0D, 0.0D, org.spigotmc.SpigotConfig.attackDamage);
    public static final IAttribute ATTACK_KNOCKBACK = new AttributeRanged((IAttribute) null, "generic.attackKnockback", 0.0D, 0.0D, 5.0D);
    public static final IAttribute ATTACK_SPEED = (new AttributeRanged((IAttribute) null, "generic.attackSpeed", 4.0D, 0.0D, 1024.0D)).a(true);
    public static final IAttribute ARMOR = (new AttributeRanged((IAttribute) null, "generic.armor", 0.0D, 0.0D, 30.0D)).a(true);
    public static final IAttribute ARMOR_TOUGHNESS = (new AttributeRanged((IAttribute) null, "generic.armorToughness", 0.0D, 0.0D, 20.0D)).a(true);
    public static final IAttribute LUCK = (new AttributeRanged((IAttribute) null, "generic.luck", 0.0D, -1024.0D, 1024.0D)).a(true);
    // Spigot end

    public static NBTTagList a(AttributeMapBase attributemapbase) {
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = attributemapbase.a().iterator();

        while (iterator.hasNext()) {
            AttributeInstance attributeinstance = (AttributeInstance) iterator.next();

            nbttaglist.add(a(attributeinstance));
        }

        return nbttaglist;
    }

    private static NBTTagCompound a(AttributeInstance attributeinstance) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        IAttribute iattribute = attributeinstance.getAttribute();

        nbttagcompound.setString("Name", iattribute.getName());
        nbttagcompound.setDouble("Base", attributeinstance.getBaseValue());
        Collection<AttributeModifier> collection = attributeinstance.getModifiers();

        if (collection != null && !collection.isEmpty()) {
            NBTTagList nbttaglist = new NBTTagList();
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                AttributeModifier attributemodifier = (AttributeModifier) iterator.next();

                if (attributemodifier.e()) {
                    nbttaglist.add(a(attributemodifier));
                }
            }

            nbttagcompound.set("Modifiers", nbttaglist);
        }

        return nbttagcompound;
    }

    public static NBTTagCompound a(AttributeModifier attributemodifier) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.setString("Name", attributemodifier.getName());
        nbttagcompound.setDouble("Amount", attributemodifier.getAmount());
        nbttagcompound.setInt("Operation", attributemodifier.getOperation().a());
        nbttagcompound.a("UUID", attributemodifier.getUniqueId());
        return nbttagcompound;
    }

    public static void a(AttributeMapBase attributemapbase, NBTTagList nbttaglist) {
        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
            AttributeInstance attributeinstance = attributemapbase.a(nbttagcompound.getString("Name"));

            if (attributeinstance == null) {
                GenericAttributes.LOGGER.warn("Ignoring unknown attribute '{}'", nbttagcompound.getString("Name"));
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
                    AttributeModifier attributemodifier1 = attributeinstance.a(attributemodifier.getUniqueId());

                    if (attributemodifier1 != null) {
                        attributeinstance.removeModifier(attributemodifier1);
                    }

                    attributeinstance.addModifier(attributemodifier);
                }
            }
        }

    }

    @Nullable
    public static AttributeModifier a(NBTTagCompound nbttagcompound) {
        UUID uuid = nbttagcompound.a("UUID");

        try {
            AttributeModifier.Operation attributemodifier_operation = AttributeModifier.Operation.a(nbttagcompound.getInt("Operation"));

            return new AttributeModifier(uuid, nbttagcompound.getString("Name"), nbttagcompound.getDouble("Amount"), attributemodifier_operation);
        } catch (Exception exception) {
            GenericAttributes.LOGGER.warn("Unable to create attribute: {}", exception.getMessage());
            return null;
        }
    }
}
