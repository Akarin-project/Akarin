package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.craftbukkit.event.CraftEventFactory; // CraftBukkit

public class RecipeBookServer extends RecipeBook {

    private static final Logger g = LogManager.getLogger();
    private final CraftingManager h;

    public RecipeBookServer(CraftingManager craftingmanager) {
        this.h = craftingmanager;
    }

    public int a(Collection<IRecipe> collection, EntityPlayer entityplayer) {
        List<MinecraftKey> list = Lists.newArrayList();
        int i = 0;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            IRecipe irecipe = (IRecipe) iterator.next();
            MinecraftKey minecraftkey = irecipe.getKey();

            if (!this.a.contains(minecraftkey) && !irecipe.c() && CraftEventFactory.handlePlayerRecipeListUpdateEvent(entityplayer, minecraftkey)) { // CraftBukkit
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

    public int b(Collection<IRecipe> collection, EntityPlayer entityplayer) {
        List<MinecraftKey> list = Lists.newArrayList();
        int i = 0;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            IRecipe irecipe = (IRecipe) iterator.next();
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

    public NBTTagCompound e() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.setBoolean("isGuiOpen", this.c);
        nbttagcompound.setBoolean("isFilteringCraftable", this.d);
        nbttagcompound.setBoolean("isFurnaceGuiOpen", this.e);
        nbttagcompound.setBoolean("isFurnaceFilteringCraftable", this.f);
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = this.a.iterator();

        while (iterator.hasNext()) {
            MinecraftKey minecraftkey = (MinecraftKey) iterator.next();

            nbttaglist.add((NBTBase) (new NBTTagString(minecraftkey.toString())));
        }

        nbttagcompound.set("recipes", nbttaglist);
        NBTTagList nbttaglist1 = new NBTTagList();
        Iterator iterator1 = this.b.iterator();

        while (iterator1.hasNext()) {
            MinecraftKey minecraftkey1 = (MinecraftKey) iterator1.next();

            nbttaglist1.add((NBTBase) (new NBTTagString(minecraftkey1.toString())));
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

        for (int i = 0; i < nbttaglist.size(); ++i) {
            MinecraftKey minecraftkey = new MinecraftKey(nbttaglist.getString(i));
            IRecipe irecipe = this.h.a(minecraftkey);

            if (irecipe == null) {
                RecipeBookServer.g.error("Tried to load unrecognized recipe: {} removed now.", minecraftkey);
            } else {
                this.a(irecipe);
            }
        }

        NBTTagList nbttaglist1 = nbttagcompound.getList("toBeDisplayed", 8);

        for (int j = 0; j < nbttaglist1.size(); ++j) {
            MinecraftKey minecraftkey1 = new MinecraftKey(nbttaglist1.getString(j));
            IRecipe irecipe1 = this.h.a(minecraftkey1);

            if (irecipe1 == null) {
                RecipeBookServer.g.error("Tried to load unrecognized recipe: {} removed now.", minecraftkey1);
            } else {
                this.f(irecipe1);
            }
        }

    }

    public void a(EntityPlayer entityplayer) {
        entityplayer.playerConnection.sendPacket(new PacketPlayOutRecipes(PacketPlayOutRecipes.Action.INIT, this.a, this.b, this.c, this.d, this.e, this.f));
    }
}
