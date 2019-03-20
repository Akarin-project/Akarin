package net.minecraft.server;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class EntitySelector {

    private final int a;
    private final boolean b;
    private final boolean c;
    private final Predicate<Entity> d;
    private final CriterionConditionValue.FloatRange e;
    private final Function<Vec3D, Vec3D> f;
    @Nullable
    private final AxisAlignedBB g;
    private final BiConsumer<Vec3D, List<? extends Entity>> h;
    private final boolean i;
    @Nullable
    private final String j;
    @Nullable
    private final UUID k;
    private final Class<? extends Entity> l;
    private final boolean m;

    public EntitySelector(int i, boolean flag, boolean flag1, Predicate<Entity> predicate, CriterionConditionValue.FloatRange criterionconditionvalue_floatrange, Function<Vec3D, Vec3D> function, @Nullable AxisAlignedBB axisalignedbb, BiConsumer<Vec3D, List<? extends Entity>> biconsumer, boolean flag2, @Nullable String s, @Nullable UUID uuid, Class<? extends Entity> oclass, boolean flag3) {
        this.a = i;
        this.b = flag;
        this.c = flag1;
        this.d = predicate;
        this.e = criterionconditionvalue_floatrange;
        this.f = function;
        this.g = axisalignedbb;
        this.h = biconsumer;
        this.i = flag2;
        this.j = s;
        this.k = uuid;
        this.l = oclass;
        this.m = flag3;
    }

    public int a() {
        return this.a;
    }

    public boolean b() {
        return this.b;
    }

    public boolean c() {
        return this.i;
    }

    public boolean d() {
        return this.c;
    }

    private void e(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
        if (this.m && !commandlistenerwrapper.hasPermission(2, "minecraft.command.selector")) { // CraftBukkit
            throw ArgumentEntity.f.create();
        }
    }

    public Entity a(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
        this.e(commandlistenerwrapper);
        List<? extends Entity> list = this.b(commandlistenerwrapper);

        if (list.isEmpty()) {
            throw ArgumentEntity.d.create();
        } else if (list.size() > 1) {
            throw ArgumentEntity.a.create();
        } else {
            return (Entity) list.get(0);
        }
    }

    public List<? extends Entity> b(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
        this.e(commandlistenerwrapper);
        if (!this.b) {
            return this.d(commandlistenerwrapper);
        } else if (this.j != null) {
            EntityPlayer entityplayer = commandlistenerwrapper.getServer().getPlayerList().getPlayer(this.j);

            return (List) (entityplayer == null ? Collections.emptyList() : Lists.newArrayList(new EntityPlayer[] { entityplayer}));
        } else if (this.k != null) {
            Iterator iterator = commandlistenerwrapper.getServer().getWorlds().iterator();

            Entity entity;

            do {
                if (!iterator.hasNext()) {
                    return Collections.emptyList();
                }

                WorldServer worldserver = (WorldServer) iterator.next();

                entity = worldserver.getEntity(this.k);
            } while (entity == null);

            return Lists.newArrayList(new Entity[] { entity});
        } else {
            Vec3D vec3d = (Vec3D) this.f.apply(commandlistenerwrapper.getPosition());
            Predicate<Entity> predicate = this.a(vec3d);

            if (this.i) {
                return (List) (commandlistenerwrapper.getEntity() != null && predicate.test(commandlistenerwrapper.getEntity()) ? Lists.newArrayList(new Entity[] { commandlistenerwrapper.getEntity()}) : Collections.emptyList());
            } else {
                List<Entity> list = Lists.newArrayList();

                if (this.d()) {
                    this.a(list, commandlistenerwrapper.getWorld(), vec3d, predicate);
                } else {
                    Iterator iterator1 = commandlistenerwrapper.getServer().getWorlds().iterator();

                    while (iterator1.hasNext()) {
                        WorldServer worldserver1 = (WorldServer) iterator1.next();

                        this.a(list, worldserver1, vec3d, predicate);
                    }
                }

                return this.a(vec3d, (List) list);
            }
        }
    }

    private void a(List<Entity> list, WorldServer worldserver, Vec3D vec3d, Predicate<Entity> predicate) {
        Class oclass;

        if (this.g != null) {
            oclass = this.l;
            AxisAlignedBB axisalignedbb = this.g.a(vec3d);

            predicate.getClass();
            list.addAll(worldserver.a(oclass, axisalignedbb, (java.util.function.Predicate<Entity>) predicate::test)); // CraftBukkit - decompile error
        } else {
            oclass = this.l;
            predicate.getClass();
            list.addAll(worldserver.a(oclass, predicate::test));
        }

    }

    public EntityPlayer c(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
        this.e(commandlistenerwrapper);
        List<EntityPlayer> list = this.d(commandlistenerwrapper);

        if (list.size() != 1) {
            throw ArgumentEntity.e.create();
        } else {
            return (EntityPlayer) list.get(0);
        }
    }

    public List<EntityPlayer> d(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
        this.e(commandlistenerwrapper);
        EntityPlayer entityplayer;

        if (this.j != null) {
            entityplayer = commandlistenerwrapper.getServer().getPlayerList().getPlayer(this.j);
            return (List) (entityplayer == null ? Collections.emptyList() : Lists.newArrayList(new EntityPlayer[] { entityplayer}));
        } else if (this.k != null) {
            entityplayer = commandlistenerwrapper.getServer().getPlayerList().a(this.k);
            return (List) (entityplayer == null ? Collections.emptyList() : Lists.newArrayList(new EntityPlayer[] { entityplayer}));
        } else {
            Vec3D vec3d = (Vec3D) this.f.apply(commandlistenerwrapper.getPosition());
            Predicate<Entity> predicate = this.a(vec3d);

            if (this.i) {
                if (commandlistenerwrapper.getEntity() instanceof EntityPlayer) {
                    EntityPlayer entityplayer1 = (EntityPlayer) commandlistenerwrapper.getEntity();

                    if (predicate.test(entityplayer1)) {
                        return Lists.newArrayList(new EntityPlayer[] { entityplayer1});
                    }
                }

                return Collections.emptyList();
            } else {
                Object object;

                if (this.d()) {
                    WorldServer worldserver = commandlistenerwrapper.getWorld();

                    predicate.getClass();
                    object = worldserver.b(EntityPlayer.class, predicate::test);
                } else {
                    object = Lists.newArrayList();
                    Iterator iterator = commandlistenerwrapper.getServer().getPlayerList().v().iterator();

                    while (iterator.hasNext()) {
                        EntityPlayer entityplayer2 = (EntityPlayer) iterator.next();

                        if (predicate.test(entityplayer2)) {
                            ((List) object).add(entityplayer2);
                        }
                    }
                }

                return this.a(vec3d, (List) object);
            }
        }
    }

    private Predicate<Entity> a(Vec3D vec3d) {
        Predicate<Entity> predicate = this.d;

        if (this.g != null) {
            AxisAlignedBB axisalignedbb = this.g.a(vec3d);

            predicate = predicate.and((entity) -> {
                return axisalignedbb.c(entity.getBoundingBox());
            });
        }

        if (!this.e.c()) {
            predicate = predicate.and((entity) -> {
                return this.e.a(entity.a(vec3d));
            });
        }

        return predicate;
    }

    private <T extends Entity> List<T> a(Vec3D vec3d, List<T> list) {
        if (list.size() > 1) {
            this.h.accept(vec3d, list);
        }

        return list.subList(0, Math.min(this.a, list.size()));
    }

    public static IChatBaseComponent a(List<? extends Entity> list) {
        return ChatComponentUtils.b(list, Entity::getScoreboardDisplayName);
    }
}
