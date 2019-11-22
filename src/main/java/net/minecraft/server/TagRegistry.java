package net.minecraft.server;

import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class TagRegistry implements IReloadListener {

    private final TagsServer<Block> blockTags;
    private final TagsServer<Item> itemTags;
    private final TagsServer<FluidType> fluidTags;
    private final TagsServer<EntityTypes<?>> entityTags;

    public TagRegistry() {
        this.blockTags = new TagsServer<>(IRegistry.BLOCK, "tags/blocks", "block");
        this.itemTags = new TagsServer<>(IRegistry.ITEM, "tags/items", "item");
        this.fluidTags = new TagsServer<>(IRegistry.FLUID, "tags/fluids", "fluid");
        this.entityTags = new TagsServer<>(IRegistry.ENTITY_TYPE, "tags/entity_types", "entity_type");
    }

    public TagsServer<Block> getBlockTags() {
        return this.blockTags;
    }

    public TagsServer<Item> getItemTags() {
        return this.itemTags;
    }

    public TagsServer<FluidType> getFluidTags() {
        return this.fluidTags;
    }

    public TagsServer<EntityTypes<?>> getEntityTags() {
        return this.entityTags;
    }

    public void a(PacketDataSerializer packetdataserializer) {
        this.blockTags.a(packetdataserializer);
        this.itemTags.a(packetdataserializer);
        this.fluidTags.a(packetdataserializer);
        this.entityTags.a(packetdataserializer);
    }

    public static TagRegistry b(PacketDataSerializer packetdataserializer) {
        TagRegistry tagregistry = new TagRegistry();

        tagregistry.getBlockTags().b(packetdataserializer);
        tagregistry.getItemTags().b(packetdataserializer);
        tagregistry.getFluidTags().b(packetdataserializer);
        tagregistry.getEntityTags().b(packetdataserializer);
        return tagregistry;
    }

    @Override
    public CompletableFuture<Void> a(IReloadListener.a ireloadlistener_a, IResourceManager iresourcemanager, GameProfilerFiller gameprofilerfiller, GameProfilerFiller gameprofilerfiller1, Executor executor, Executor executor1) {
        CompletableFuture<Map<MinecraftKey, Tag.a<Block>>> completablefuture = this.blockTags.a(iresourcemanager, executor);
        CompletableFuture<Map<MinecraftKey, Tag.a<Item>>> completablefuture1 = this.itemTags.a(iresourcemanager, executor);
        CompletableFuture<Map<MinecraftKey, Tag.a<FluidType>>> completablefuture2 = this.fluidTags.a(iresourcemanager, executor);
        CompletableFuture<Map<MinecraftKey, Tag.a<EntityTypes<?>>>> completablefuture3 = this.entityTags.a(iresourcemanager, executor);
        CompletableFuture<TagRegistry.a> completablefuture4 = completablefuture.thenCombine(completablefuture1, Pair::of).thenCombine(completablefuture2.thenCombine(completablefuture3, Pair::of), (pair, pair1) -> { // CraftBukkit - decompile error
            return new TagRegistry.a((Map) pair.getFirst(), (Map) pair.getSecond(), (Map) pair1.getFirst(), (Map) pair1.getSecond());
        });

        ireloadlistener_a.getClass();
        return completablefuture4.thenCompose(ireloadlistener_a::a).thenAcceptAsync((tagregistry_a) -> {
            this.blockTags.a(tagregistry_a.a);
            this.itemTags.a(tagregistry_a.b);
            this.fluidTags.a(tagregistry_a.c);
            this.entityTags.a(tagregistry_a.d);
            TagsBlock.a((Tags) this.blockTags);
            TagsItem.a((Tags) this.itemTags);
            TagsFluid.a((Tags) this.fluidTags);
            TagsEntity.a((Tags) this.entityTags);
            // CraftBukkit start
            this.blockTags.version++;
            this.itemTags.version++;
            this.fluidTags.version++;
            this.entityTags.version++;
            // CraftBukkit end
        }, executor1);
    }

    public static class a {

        final Map<MinecraftKey, Tag.a<Block>> a;
        final Map<MinecraftKey, Tag.a<Item>> b;
        final Map<MinecraftKey, Tag.a<FluidType>> c;
        final Map<MinecraftKey, Tag.a<EntityTypes<?>>> d;

        public a(Map<MinecraftKey, Tag.a<Block>> map, Map<MinecraftKey, Tag.a<Item>> map1, Map<MinecraftKey, Tag.a<FluidType>> map2, Map<MinecraftKey, Tag.a<EntityTypes<?>>> map3) {
            this.a = map;
            this.b = map1;
            this.c = map2;
            this.d = map3;
        }
    }
}
