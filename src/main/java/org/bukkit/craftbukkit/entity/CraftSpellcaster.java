package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import net.minecraft.server.EntityIllagerWizard;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Spellcaster;

public class CraftSpellcaster extends CraftIllager implements Spellcaster {

    public CraftSpellcaster(CraftServer server, EntityIllagerWizard entity) {
        super(server, entity);
    }

    @Override
    public EntityIllagerWizard getHandle() {
        return (EntityIllagerWizard) super.getHandle();
    }

    @Override
    public String toString() {
        return "CraftSpellcaster";
    }

    @Override
    public Spell getSpell() {
        return Spell.valueOf(getHandle().getSpell().name());
    }

    @Override
    public void setSpell(Spell spell) {
        Preconditions.checkArgument(spell != null, "Use Spell.NONE");

        getHandle().setSpell(EntityIllagerWizard.Spell.a(spell.ordinal()));
    }
}
