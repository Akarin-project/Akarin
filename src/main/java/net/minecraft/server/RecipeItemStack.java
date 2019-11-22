package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;

public final class RecipeItemStack implements Predicate<ItemStack> {

    private static final Predicate<? super RecipeItemStack.Provider> b = (recipeitemstack_provider) -> {
        return !recipeitemstack_provider.a().stream().allMatch(ItemStack::isEmpty);
    };
    public static final RecipeItemStack a = new RecipeItemStack(Stream.empty());
    private final RecipeItemStack.Provider[] c;
    public ItemStack[] choices;
    private IntList e;
    public boolean exact; // CraftBukkit

    public RecipeItemStack(Stream<? extends RecipeItemStack.Provider> stream) {
        this.c = (RecipeItemStack.Provider[]) stream.filter(RecipeItemStack.b).toArray((i) -> {
            return new RecipeItemStack.Provider[i];
        });
    }

    public void buildChoices() {
        if (this.choices == null) {
            this.choices = (ItemStack[]) Arrays.stream(this.c).flatMap((recipeitemstack_provider) -> {
                return recipeitemstack_provider.a().stream();
            }).distinct().toArray((i) -> {
                return new ItemStack[i];
            });
        }

    }

    public boolean test(@Nullable ItemStack itemstack) {
        if (itemstack == null) {
            return false;
        } else if (this.c.length == 0) {
            return itemstack.isEmpty();
        } else {
            this.buildChoices();
            ItemStack[] aitemstack = this.choices;
            int i = aitemstack.length;

            for (int j = 0; j < i; ++j) {
                ItemStack itemstack1 = aitemstack[j];

                // CraftBukkit start
                if (exact) {
                    if (itemstack1.getItem() == itemstack.getItem() && ItemStack.equals(itemstack, itemstack1)) {
                        return true;
                    }

                    continue;
                }
                // CraftBukkit end
                if (itemstack1.getItem() == itemstack.getItem()) {
                    return true;
                }
            }

            return false;
        }
    }

    public IntList b() {
        if (this.e == null) {
            this.buildChoices();
            this.e = new IntArrayList(this.choices.length);
            ItemStack[] aitemstack = this.choices;
            int i = aitemstack.length;

            for (int j = 0; j < i; ++j) {
                ItemStack itemstack = aitemstack[j];

                this.e.add(AutoRecipeStackManager.c(itemstack));
            }

            this.e.sort(IntComparators.NATURAL_COMPARATOR);
        }

        return this.e;
    }

    public void a(PacketDataSerializer packetdataserializer) {
        this.buildChoices();
        packetdataserializer.d(this.choices.length);

        for (int i = 0; i < this.choices.length; ++i) {
            packetdataserializer.a(this.choices[i]);
        }

    }

    public JsonElement c() {
        if (this.c.length == 1) {
            return this.c[0].b();
        } else {
            JsonArray jsonarray = new JsonArray();
            RecipeItemStack.Provider[] arecipeitemstack_provider = this.c;
            int i = arecipeitemstack_provider.length;

            for (int j = 0; j < i; ++j) {
                RecipeItemStack.Provider recipeitemstack_provider = arecipeitemstack_provider[j];

                jsonarray.add(recipeitemstack_provider.b());
            }

            return jsonarray;
        }
    }

    public boolean d() {
        return this.c.length == 0 && (this.choices == null || this.choices.length == 0) && (this.e == null || this.e.isEmpty());
    }

    private static RecipeItemStack a(Stream<? extends RecipeItemStack.Provider> stream) {
        RecipeItemStack recipeitemstack = new RecipeItemStack(stream);

        return recipeitemstack.c.length == 0 ? RecipeItemStack.a : recipeitemstack;
    }

    public static RecipeItemStack a(IMaterial... aimaterial) {
        return a(Arrays.stream(aimaterial).map((imaterial) -> {
            return new RecipeItemStack.StackProvider(new ItemStack(imaterial));
        }));
    }

    public static RecipeItemStack a(Tag<Item> tag) {
        return a(Stream.of(new RecipeItemStack.b(tag)));
    }

    public static RecipeItemStack b(PacketDataSerializer packetdataserializer) {
        int i = packetdataserializer.i();

        return a(Stream.generate(() -> {
            return new RecipeItemStack.StackProvider(packetdataserializer.m());
        }).limit((long) i));
    }

    public static RecipeItemStack a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            if (jsonelement.isJsonObject()) {
                return a(Stream.of(a(jsonelement.getAsJsonObject())));
            } else if (jsonelement.isJsonArray()) {
                JsonArray jsonarray = jsonelement.getAsJsonArray();

                if (jsonarray.size() == 0) {
                    throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
                } else {
                    return a(StreamSupport.stream(jsonarray.spliterator(), false).map((jsonelement1) -> {
                        return a(ChatDeserializer.m(jsonelement1, "item"));
                    }));
                }
            } else {
                throw new JsonSyntaxException("Expected item to be object or array of objects");
            }
        } else {
            throw new JsonSyntaxException("Item cannot be null");
        }
    }

    public static RecipeItemStack.Provider a(JsonObject jsonobject) {
        if (jsonobject.has("item") && jsonobject.has("tag")) {
            throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
        } else {
            MinecraftKey minecraftkey;

            if (jsonobject.has("item")) {
                minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "item"));
                Item item = (Item) IRegistry.ITEM.getOptional(minecraftkey).orElseThrow(() -> {
                    return new JsonSyntaxException("Unknown item '" + minecraftkey + "'");
                });

                return new RecipeItemStack.StackProvider(new ItemStack(item));
            } else if (jsonobject.has("tag")) {
                minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "tag"));
                Tag<Item> tag = TagsItem.a().a(minecraftkey);

                if (tag == null) {
                    throw new JsonSyntaxException("Unknown item tag '" + minecraftkey + "'");
                } else {
                    return new RecipeItemStack.b(tag);
                }
            } else {
                throw new JsonParseException("An ingredient entry needs either a tag or an item");
            }
        }
    }

    static class b implements RecipeItemStack.Provider {

        private final Tag<Item> a;

        private b(Tag<Item> tag) {
            this.a = tag;
        }

        @Override
        public Collection<ItemStack> a() {
            List<ItemStack> list = Lists.newArrayList();
            Iterator iterator = this.a.a().iterator();

            while (iterator.hasNext()) {
                Item item = (Item) iterator.next();

                list.add(new ItemStack(item));
            }

            return list;
        }

        @Override
        public JsonObject b() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.addProperty("tag", this.a.c().toString());
            return jsonobject;
        }
    }

    public static class StackProvider implements RecipeItemStack.Provider {

        private final ItemStack a;

        public StackProvider(ItemStack itemstack) {
            this.a = itemstack;
        }

        @Override
        public Collection<ItemStack> a() {
            return Collections.singleton(this.a);
        }

        @Override
        public JsonObject b() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.addProperty("item", IRegistry.ITEM.getKey(this.a.getItem()).toString());
            return jsonobject;
        }
    }

    public interface Provider {

        Collection<ItemStack> a();

        JsonObject b();
    }
}
