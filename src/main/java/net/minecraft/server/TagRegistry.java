package net.minecraft.server;

public class TagRegistry implements IResourcePackListener {

    private final TagsServer<Block> a;
    private final TagsServer<Item> b;
    private final TagsServer<FluidType> c;

    public TagRegistry() {
        this.a = new TagsServer<>(IRegistry.BLOCK, "tags/blocks", "block");
        this.b = new TagsServer<>(IRegistry.ITEM, "tags/items", "item");
        this.c = new TagsServer<>(IRegistry.FLUID, "tags/fluids", "fluid");
    }

    public TagsServer<Block> a() {
        return this.a;
    }

    public TagsServer<Item> b() {
        return this.b;
    }

    public TagsServer<FluidType> c() {
        return this.c;
    }

    public void d() {
        this.a.b();
        this.b.b();
        this.c.b();
    }

    public void a(IResourceManager iresourcemanager) {
        this.d();
        this.a.a(iresourcemanager);
        this.b.a(iresourcemanager);
        this.c.a(iresourcemanager);
        TagsBlock.a((Tags) this.a);
        TagsItem.a((Tags) this.b);
        TagsFluid.a((Tags) this.c);
    }

    public void a(PacketDataSerializer packetdataserializer) {
        this.a.a(packetdataserializer);
        this.b.a(packetdataserializer);
        this.c.a(packetdataserializer);
    }

    public static TagRegistry b(PacketDataSerializer packetdataserializer) {
        TagRegistry tagregistry = new TagRegistry();

        tagregistry.a().b(packetdataserializer);
        tagregistry.b().b(packetdataserializer);
        tagregistry.c().b(packetdataserializer);
        return tagregistry;
    }
}
