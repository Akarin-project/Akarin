package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class EntityPainting extends EntityHanging {

    public Paintings art;

    public EntityPainting(World world) {
        super(EntityTypes.PAINTING, world);
    }

    public EntityPainting(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        super(EntityTypes.PAINTING, world, blockposition);
        List<Paintings> list = Lists.newArrayList();
        int i = 0;
        Iterator iterator = IRegistry.MOTIVE.iterator();

        Paintings paintings;

        while (iterator.hasNext()) {
            paintings = (Paintings) iterator.next();
            this.art = paintings;
            this.setDirection(enumdirection);
            if (this.survives()) {
                list.add(paintings);
                int j = paintings.b() * paintings.c();

                if (j > i) {
                    i = j;
                }
            }
        }

        if (!list.isEmpty()) {
            iterator = list.iterator();

            while (iterator.hasNext()) {
                paintings = (Paintings) iterator.next();
                if (paintings.b() * paintings.c() < i) {
                    iterator.remove();
                }
            }

            this.art = (Paintings) list.get(this.random.nextInt(list.size()));
        }

        this.setDirection(enumdirection);
    }

    public void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setString("Motive", IRegistry.MOTIVE.getKey(this.art).toString());
        super.b(nbttagcompound);
    }

    public void a(NBTTagCompound nbttagcompound) {
        this.art = (Paintings) IRegistry.MOTIVE.getOrDefault(MinecraftKey.a(nbttagcompound.getString("Motive")));
        super.a(nbttagcompound);
    }

    public int getWidth() {
        return this.art.b();
    }

    public int getHeight() {
        return this.art.c();
    }

    public void a(@Nullable Entity entity) {
        if (this.world.getGameRules().getBoolean("doEntityDrops")) {
            this.a(SoundEffects.ENTITY_PAINTING_BREAK, 1.0F, 1.0F);
            if (entity instanceof EntityHuman) {
                EntityHuman entityhuman = (EntityHuman) entity;

                if (entityhuman.abilities.canInstantlyBuild) {
                    return;
                }
            }

            this.a((IMaterial) Items.PAINTING);
        }
    }

    public void m() {
        this.a(SoundEffects.ENTITY_PAINTING_PLACE, 1.0F, 1.0F);
    }

    public void setPositionRotation(double d0, double d1, double d2, float f, float f1) {
        this.setPosition(d0, d1, d2);
    }
}
