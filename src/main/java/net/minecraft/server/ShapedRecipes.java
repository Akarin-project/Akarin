package net.minecraft.server;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class ShapedRecipes implements IRecipe {

    private final int width;
    private final int height;
    private final NonNullList<RecipeItemStack> items;
    private final ItemStack result;
    private final MinecraftKey key;
    private final String group;

    public ShapedRecipes(MinecraftKey minecraftkey, String s, int i, int j, NonNullList<RecipeItemStack> nonnulllist, ItemStack itemstack) {
        this.key = minecraftkey;
        this.group = s;
        this.width = i;
        this.height = j;
        this.items = nonnulllist;
        this.result = itemstack;
    }

    public MinecraftKey getKey() {
        return this.key;
    }

    public RecipeSerializer<?> a() {
        return RecipeSerializers.a;
    }

    public ItemStack d() {
        return this.result;
    }

    public NonNullList<RecipeItemStack> e() {
        return this.items;
    }

    public boolean a(IInventory iinventory, World world) {
        if (!(iinventory instanceof InventoryCrafting)) {
            return false;
        } else {
            for (int i = 0; i <= iinventory.U_() - this.width; ++i) {
                for (int j = 0; j <= iinventory.n() - this.height; ++j) {
                    if (this.a(iinventory, i, j, true)) {
                        return true;
                    }

                    if (this.a(iinventory, i, j, false)) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    private boolean a(IInventory iinventory, int i, int j, boolean flag) {
        for (int k = 0; k < iinventory.U_(); ++k) {
            for (int l = 0; l < iinventory.n(); ++l) {
                int i1 = k - i;
                int j1 = l - j;
                RecipeItemStack recipeitemstack = RecipeItemStack.a;

                if (i1 >= 0 && j1 >= 0 && i1 < this.width && j1 < this.height) {
                    if (flag) {
                        recipeitemstack = (RecipeItemStack) this.items.get(this.width - i1 - 1 + j1 * this.width);
                    } else {
                        recipeitemstack = (RecipeItemStack) this.items.get(i1 + j1 * this.width);
                    }
                }

                if (!recipeitemstack.test(iinventory.getItem(k + l * iinventory.U_()))) {
                    return false;
                }
            }
        }

        return true;
    }

    public ItemStack craftItem(IInventory iinventory) {
        return this.d().cloneItemStack();
    }

    public int g() {
        return this.width;
    }

    public int h() {
        return this.height;
    }

    private static NonNullList<RecipeItemStack> b(String[] astring, Map<String, RecipeItemStack> map, int i, int j) {
        NonNullList<RecipeItemStack> nonnulllist = NonNullList.a(i * j, RecipeItemStack.a);
        Set<String> set = Sets.newHashSet(map.keySet());

        set.remove(" ");

        for (int k = 0; k < astring.length; ++k) {
            for (int l = 0; l < astring[k].length(); ++l) {
                String s = astring[k].substring(l, l + 1);
                RecipeItemStack recipeitemstack = (RecipeItemStack) map.get(s);

                if (recipeitemstack == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + s + "' but it's not defined in the key");
                }

                set.remove(s);
                nonnulllist.set(l + i * k, recipeitemstack);
            }
        }

        if (!set.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
        } else {
            return nonnulllist;
        }
    }

    @VisibleForTesting
    static String[] a(String... astring) {
        int i = Integer.MAX_VALUE;
        int j = 0;
        int k = 0;
        int l = 0;

        for (int i1 = 0; i1 < astring.length; ++i1) {
            String s = astring[i1];

            i = Math.min(i, a(s));
            int j1 = b(s);

            j = Math.max(j, j1);
            if (j1 < 0) {
                if (k == i1) {
                    ++k;
                }

                ++l;
            } else {
                l = 0;
            }
        }

        if (astring.length == l) {
            return new String[0];
        } else {
            String[] astring1 = new String[astring.length - l - k];

            for (int k1 = 0; k1 < astring1.length; ++k1) {
                astring1[k1] = astring[k1 + k].substring(i, j + 1);
            }

            return astring1;
        }
    }

    private static int a(String s) {
        int i;

        for (i = 0; i < s.length() && s.charAt(i) == ' '; ++i) {
            ;
        }

        return i;
    }

    private static int b(String s) {
        int i;

        for (i = s.length() - 1; i >= 0 && s.charAt(i) == ' '; --i) {
            ;
        }

        return i;
    }

    private static String[] b(JsonArray jsonarray) {
        String[] astring = new String[jsonarray.size()];

        if (astring.length > 3) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
        } else if (astring.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        } else {
            for (int i = 0; i < astring.length; ++i) {
                String s = ChatDeserializer.a(jsonarray.get(i), "pattern[" + i + "]");

                if (s.length() > 3) {
                    throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
                }

                if (i > 0 && astring[0].length() != s.length()) {
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                }

                astring[i] = s;
            }

            return astring;
        }
    }

    private static Map<String, RecipeItemStack> c(JsonObject jsonobject) {
        Map<String, RecipeItemStack> map = Maps.newHashMap();
        Iterator iterator = jsonobject.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<String, JsonElement> entry = (Entry) iterator.next();

            if (((String) entry.getKey()).length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + (String) entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }

            if (" ".equals(entry.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }

            map.put(entry.getKey(), RecipeItemStack.a((JsonElement) entry.getValue()));
        }

        map.put(" ", RecipeItemStack.a);
        return map;
    }

    public static ItemStack a(JsonObject jsonobject) {
        String s = ChatDeserializer.h(jsonobject, "item");
        Item item = (Item) IRegistry.ITEM.get(new MinecraftKey(s));

        if (item == null) {
            throw new JsonSyntaxException("Unknown item '" + s + "'");
        } else if (jsonobject.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        } else {
            int i = ChatDeserializer.a(jsonobject, "count", 1);

            return new ItemStack(item, i);
        }
    }

    public static class a implements RecipeSerializer<ShapedRecipes> {

        public a() {}

        public ShapedRecipes a(MinecraftKey minecraftkey, JsonObject jsonobject) {
            String s = ChatDeserializer.a(jsonobject, "group", "");
            Map<String, RecipeItemStack> map = ShapedRecipes.c(ChatDeserializer.t(jsonobject, "key"));
            String[] astring = ShapedRecipes.a(ShapedRecipes.b(ChatDeserializer.u(jsonobject, "pattern")));
            int i = astring[0].length();
            int j = astring.length;
            NonNullList<RecipeItemStack> nonnulllist = ShapedRecipes.b(astring, map, i, j);
            ItemStack itemstack = ShapedRecipes.a(ChatDeserializer.t(jsonobject, "result"));

            return new ShapedRecipes(minecraftkey, s, i, j, nonnulllist, itemstack);
        }

        public String a() {
            return "crafting_shaped";
        }

        public ShapedRecipes a(MinecraftKey minecraftkey, PacketDataSerializer packetdataserializer) {
            int i = packetdataserializer.g();
            int j = packetdataserializer.g();
            String s = packetdataserializer.e(32767);
            NonNullList<RecipeItemStack> nonnulllist = NonNullList.a(i * j, RecipeItemStack.a);

            for (int k = 0; k < nonnulllist.size(); ++k) {
                nonnulllist.set(k, RecipeItemStack.b(packetdataserializer));
            }

            ItemStack itemstack = packetdataserializer.k();

            return new ShapedRecipes(minecraftkey, s, i, j, nonnulllist, itemstack);
        }

        public void a(PacketDataSerializer packetdataserializer, ShapedRecipes shapedrecipes) {
            packetdataserializer.d(shapedrecipes.width);
            packetdataserializer.d(shapedrecipes.height);
            packetdataserializer.a(shapedrecipes.group);
            Iterator iterator = shapedrecipes.items.iterator();

            while (iterator.hasNext()) {
                RecipeItemStack recipeitemstack = (RecipeItemStack) iterator.next();

                recipeitemstack.a(packetdataserializer);
            }

            packetdataserializer.a(shapedrecipes.result);
        }
    }
}
