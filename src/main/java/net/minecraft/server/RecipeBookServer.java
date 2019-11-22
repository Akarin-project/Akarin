package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.craftbukkit.event.CraftEventFactory; // CraftBukkit

public class RecipeBookServer extends RecipeBook {

    private static final Logger LOGGER = LogManager.getLogger();
    private final CraftingManager l;

    public RecipeBookServer(CraftingManager craftingmanager) {
        this.l = craftingmanager;
    }

    public int a(Collection<IRecipe<?>> collection, EntityPlayer entityplayer) {
        List<MinecraftKey> list = Lists.newArrayList();
        int i = 0;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            IRecipe<?> irecipe = (IRecipe) iterator.next();
            MinecraftKey minecraftkey = irecipe.getKey();

            if (!this.a.contains(minecraftkey) && !irecipe.isComplex() && CraftEventFactory.handlePlayerRecipeListUpdateEvent(entityplayer, minecraftkey)) { // CraftBukkit
                this.a(minecraftkey);
                this.c(minecraftkey);
                list.add(minecraftkey);
                CriterionTriggers.f.a(entityplayer, irecipe);
                ++i;
            }
        }

        this.a(PacketPlayOutRecipes.Action.ADD, entityplayer, list);
        return i;
    }

    public int b(Collection<IRecipe<?>> collection, EntityPlayer entityplayer) {
        List<MinecraftKey> list = Lists.newArrayList();
        int i = 0;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            IRecipe<?> irecipe = (IRecipe) iterator.next();
            MinecraftKey minecraftkey = irecipe.getKey();

            if (this.a.contains(minecraftkey)) {
                this.b(minecraftkey);
                list.add(minecraftkey);
                ++i;
            }
        }

        this.a(PacketPlayOutRecipes.Action.REMOVE, entityplayer, list);
        return i;
    }

    private void a(PacketPlayOutRecipes.Action packetplayoutrecipes_action, EntityPlayer entityplayer, List<MinecraftKey> list) {
        if (entityplayer.playerConnection == null) return; // SPIGOT-4478 during PlayerLoginEvent
        entityplayer.playerConnection.sendPacket(new PacketPlayOutRecipes(packetplayoutrecipes_action, list, Collections.emptyList(), this.c, this.d, this.e, this.f));
    }

    public NBTTagCompound save() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.setBoolean("isGuiOpen", this.c);
        nbttagcompound.setBoolean("isFilteringCraftable", this.d);
        nbttagcompound.setBoolean("isFurnaceGuiOpen", this.e);
        nbttagcompound.setBoolean("isFurnaceFilteringCraftable", this.f);
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = this.a.iterator();

        while (iterator.hasNext()) {
            MinecraftKey minecraftkey = (MinecraftKey) iterator.next();
            // Paper start - ignore missing recipes
            final Optional<? extends IRecipe<?>> recipe = this.l.a(minecraftkey);
            if (!recipe.isPresent()) continue;
            // Paper end

            nbttaglist.add(new NBTTagString(minecraftkey.toString()));
        }

        nbttagcompound.set("recipes", nbttaglist);
        NBTTagList nbttaglist1 = new NBTTagList();
        Iterator iterator1 = this.b.iterator();

        while (iterator1.hasNext()) {
            MinecraftKey minecraftkey1 = (MinecraftKey) iterator1.next();
            // Paper start - ignore missing recipes
            final Optional<? extends IRecipe<?>> recipe = this.l.a(minecraftkey1);
            if (!recipe.isPresent()) continue;
            // Paper end

            nbttaglist1.add(new NBTTagString(minecraftkey1.toString()));
        }

        nbttagcompound.set("toBeDisplayed", nbttaglist1);
        return nbttagcompound;
    }

    public void a(NBTTagCompound nbttagcompound) {
        this.c = nbttagcompound.getBoolean("isGuiOpen");
        this.d = nbttagcompound.getBoolean("isFilteringCraftable");
        this.e = nbttagcompound.getBoolean("isFurnaceGuiOpen");
        this.f = nbttagcompound.getBoolean("isFurnaceFilteringCraftable");
        NBTTagList nbttaglist = nbttagcompound.getList("recipes", 8);

        this.a(nbttaglist, this::a);
        NBTTagList nbttaglist1 = nbttagcompound.getList("toBeDisplayed", 8);

        this.a(nbttaglist1, this::f);
    }

    private void a(NBTTagList nbttaglist, Consumer<IRecipe<?>> consumer) {
        for (int i = 0; i < nbttaglist.size(); ++i) {
            String s = nbttaglist.getString(i);

            try {
                MinecraftKey minecraftkey = new MinecraftKey(s);
                Optional<? extends IRecipe<?>> optional = this.l.a(minecraftkey);

                if (!optional.isPresent()) {
                    RecipeBookServer.LOGGER.error("Tried to load unrecognized recipe: {} removed now.", minecraftkey);
                } else {
                    consumer.accept(optional.get());
                }
            } catch (ResourceKeyInvalidException resourcekeyinvalidexception) {
                RecipeBookServer.LOGGER.error("Tried to load improperly formatted recipe: {} removed now.", s);
            }
        }

    }

    public void a(EntityPlayer entityplayer) {
        entityplayer.playerConnection.sendPacket(new PacketPlayOutRecipes(PacketPlayOutRecipes.Action.INIT, this.a, this.b, this.c, this.d, this.e, this.f));
    }
}
