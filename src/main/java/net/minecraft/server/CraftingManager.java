package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CraftingManager implements IResourcePackListener {

    private static final Logger c = LogManager.getLogger();
    public static final int a = "recipes/".length();
    public static final int b = ".json".length();
    public it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap<MinecraftKey, IRecipe> recipes = new it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap<>(); // CraftBukkit
    private boolean e;

    public CraftingManager() {}

    public void a(IResourceManager iresourcemanager) {
        Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

        this.e = false;
        this.recipes.clear();
        Iterator iterator = iresourcemanager.a("recipes", (s) -> {
            return s.endsWith(".json");
        }).iterator();

        while (iterator.hasNext()) {
            MinecraftKey minecraftkey = (MinecraftKey) iterator.next();
            String s = minecraftkey.getKey();
            MinecraftKey minecraftkey1 = new MinecraftKey(minecraftkey.b(), s.substring(CraftingManager.a, s.length() - CraftingManager.b));

            try {
                IResource iresource = iresourcemanager.a(minecraftkey);
                Throwable throwable = null;

                try {
                    JsonObject jsonobject = (JsonObject) ChatDeserializer.a(gson, IOUtils.toString(iresource.b(), StandardCharsets.UTF_8), JsonObject.class);

                    if (jsonobject == null) {
                        CraftingManager.c.error("Couldn't load recipe {} as it's null or empty", minecraftkey1);
                    } else {
                        this.a(RecipeSerializers.a(minecraftkey1, jsonobject));
                    }
                } catch (Throwable throwable1) {
                    throwable = throwable1;
                    throw throwable1;
                } finally {
                    if (iresource != null) {
                        if (throwable != null) {
                            try {
                                iresource.close();
                            } catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        } else {
                            iresource.close();
                        }
                    }

                }
            } catch (IllegalArgumentException | JsonParseException jsonparseexception) {
                CraftingManager.c.error("Parsing error loading recipe {}", minecraftkey1, jsonparseexception);
                this.e = true;
            } catch (IOException ioexception) {
                CraftingManager.c.error("Couldn't read custom advancement {} from {}", minecraftkey1, minecraftkey, ioexception);
                this.e = true;
            }
        }

        CraftingManager.c.info("Loaded {} recipes", this.recipes.size());
    }

    public void a(IRecipe irecipe) {
        if (this.recipes.containsKey(irecipe.getKey())) {
            throw new IllegalStateException("Duplicate recipe ignored with ID " + irecipe.getKey());
        } else {
            this.recipes.putAndMoveToFirst(irecipe.getKey(), irecipe); // CraftBukkit - SPIGOT-4638: last recipe gets priority
        }
    }

    public ItemStack craft(IInventory iinventory, World world) {
        Iterator iterator = this.recipes.values().iterator();

        IRecipe irecipe;

        do {
            if (!iterator.hasNext()) {
                iinventory.setCurrentRecipe(null); // CraftBukkit - Clear recipe when no recipe is found
                return ItemStack.a;
            }

            irecipe = (IRecipe) iterator.next();
        } while (!irecipe.a(iinventory, world));

        iinventory.setCurrentRecipe(irecipe); // CraftBukkit
        return irecipe.craftItem(iinventory);
    }

    @Nullable
    public IRecipe b(IInventory iinventory, World world) {
        Iterator iterator = this.recipes.values().iterator();

        IRecipe irecipe;

        do {
            if (!iterator.hasNext()) {
                iinventory.setCurrentRecipe(null); // CraftBukkit - Clear recipe when no recipe is found
                return null;
            }

            irecipe = (IRecipe) iterator.next();
        } while (!irecipe.a(iinventory, world));

        iinventory.setCurrentRecipe(irecipe); // CraftBukkit
        return irecipe;
    }

    public NonNullList<ItemStack> c(IInventory iinventory, World world) {
        Iterator iterator = this.recipes.values().iterator();

        while (iterator.hasNext()) {
            IRecipe irecipe = (IRecipe) iterator.next();

            if (irecipe.a(iinventory, world)) {
                return irecipe.b(iinventory);
            }
        }

        NonNullList<ItemStack> nonnulllist = NonNullList.a(iinventory.getSize(), ItemStack.a);

        for (int i = 0; i < nonnulllist.size(); ++i) {
            nonnulllist.set(i, iinventory.getItem(i));
        }

        return nonnulllist;
    }

    @Nullable
    public IRecipe a(MinecraftKey minecraftkey) {
        return (IRecipe) this.recipes.get(minecraftkey);
    }

    public Collection<IRecipe> b() {
        return this.recipes.values();
    }

    public Collection<MinecraftKey> c() {
        return this.recipes.keySet();
    }
}
