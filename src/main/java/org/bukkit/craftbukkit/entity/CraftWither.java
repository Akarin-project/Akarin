package org.bukkit.craftbukkit.entity;

import net.minecraft.server.EntityWither;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.boss.CraftBossBar;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wither;

public class CraftWither extends CraftMonster implements Wither {

    private BossBar bossBar;

    public CraftWither(CraftServer server, EntityWither entity) {
        super(server, entity);

        if (entity.bossBattle != null) {
            this.bossBar = new CraftBossBar(entity.bossBattle);
        }
    }

    @Override
    public EntityWither getHandle() {
        return (EntityWither) entity;
    }

    @Override
    public String toString() {
        return "CraftWither";
    }

    public EntityType getType() {
        return EntityType.WITHER;
    }

    @Override
    public BossBar getBossBar() {
        return bossBar;
    }
}
