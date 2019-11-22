package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CustomFunctionData implements IResourcePackListener {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final MinecraftKey d = new MinecraftKey("tick");
    private static final MinecraftKey e = new MinecraftKey("load");
    public static final int a = "functions/".length();
    public static final int b = ".mcfunction".length();
    private final MinecraftServer server;
    private final Map<MinecraftKey, CustomFunction> g = Maps.newHashMap();
    private boolean h;
    private final ArrayDeque<CustomFunctionData.a> i = new ArrayDeque();
    private final List<CustomFunctionData.a> j = Lists.newArrayList();
    private final Tags<CustomFunction> k = new Tags<>(this::a, "tags/functions", true, "function");
    private final List<CustomFunction> l = Lists.newArrayList();
    private boolean m;

    public CustomFunctionData(MinecraftServer minecraftserver) {
        this.server = minecraftserver;
    }

    public Optional<CustomFunction> a(MinecraftKey minecraftkey) {
        return Optional.ofNullable(this.g.get(minecraftkey));
    }

    public MinecraftServer a() {
        return this.server;
    }

    public int b() {
        return this.server.getGameRules().getInt(GameRules.MAX_COMMAND_CHAIN_LENGTH);
    }

    public Map<MinecraftKey, CustomFunction> c() {
        return this.g;
    }

    public com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> d() {
        return this.server.vanillaCommandDispatcher.a(); // CraftBukkit
    }

    public void tick() {
        GameProfiler gameprofiler = this.server.getMethodProfiler();
        MinecraftKey minecraftkey = CustomFunctionData.d;

        gameprofiler.a(minecraftkey::toString);
        Iterator iterator = this.l.iterator();

        while (iterator.hasNext()) {
            CustomFunction customfunction = (CustomFunction) iterator.next();

            this.a(customfunction, this.f());
        }

        this.server.getMethodProfiler().exit();
        if (this.m) {
            this.m = false;
            Collection<CustomFunction> collection = this.h().b(CustomFunctionData.e).a();

            gameprofiler = this.server.getMethodProfiler();
            minecraftkey = CustomFunctionData.e;
            gameprofiler.a(minecraftkey::toString);
            Iterator iterator1 = collection.iterator();

            while (iterator1.hasNext()) {
                CustomFunction customfunction1 = (CustomFunction) iterator1.next();

                this.a(customfunction1, this.f());
            }

            this.server.getMethodProfiler().exit();
        }

    }

    public int a(CustomFunction customfunction, CommandListenerWrapper commandlistenerwrapper) {
        int i = this.b();

        if (this.h) {
            if (this.i.size() + this.j.size() < i) {
                this.j.add(new CustomFunctionData.a(this, commandlistenerwrapper, new CustomFunction.d(customfunction)));
            }

            return 0;
        } else {
            try (co.aikar.timings.Timing timing = customfunction.getTiming().startTiming()) { // Paper
                this.h = true;
                int j = 0;
                CustomFunction.c[] acustomfunction_c = customfunction.b();

                int k;

                for (k = acustomfunction_c.length - 1; k >= 0; --k) {
                    this.i.push(new CustomFunctionData.a(this, commandlistenerwrapper, acustomfunction_c[k]));
                }

                while (!this.i.isEmpty()) {
                    try {
                        CustomFunctionData.a customfunctiondata_a = (CustomFunctionData.a) this.i.removeFirst();

                        this.server.getMethodProfiler().a(customfunctiondata_a::toString);
                        customfunctiondata_a.a(this.i, i);
                        if (!this.j.isEmpty()) {
                            List list = Lists.reverse(this.j);
                            ArrayDeque arraydeque = this.i;

                            this.i.getClass();
                            list.forEach(arraydeque::addFirst);
                            this.j.clear();
                        }
                    } finally {
                        this.server.getMethodProfiler().exit();
                    }

                    ++j;
                    if (j >= i) {
                        k = j;
                        return k;
                    }
                }

                k = j;
                return k;
            } finally {
                this.i.clear();
                this.j.clear();
                this.h = false;
            }
        }
    }

    @Override
    public void a(IResourceManager iresourcemanager) {
        this.g.clear();
        this.l.clear();
        Collection<MinecraftKey> collection = iresourcemanager.a("functions", (s) -> {
            return s.endsWith(".mcfunction");
        });
        List<CompletableFuture<CustomFunction>> list = Lists.newArrayList();
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            MinecraftKey minecraftkey = (MinecraftKey) iterator.next();
            String s = minecraftkey.getKey();
            MinecraftKey minecraftkey1 = new MinecraftKey(minecraftkey.getNamespace(), s.substring(CustomFunctionData.a, s.length() - CustomFunctionData.b));

            list.add(CompletableFuture.supplyAsync(() -> {
                return a(iresourcemanager, minecraftkey);
            }, Resource.a).thenApplyAsync((list1) -> {
                return CustomFunction.a(minecraftkey1, this, list1);
            }, this.server.aU()).handle((customfunction, throwable) -> {
                return this.a(customfunction, throwable, minecraftkey);
            }));
        }

        CompletableFuture.allOf((CompletableFuture[]) list.toArray(new CompletableFuture[0])).join();
        if (!this.g.isEmpty()) {
            CustomFunctionData.LOGGER.info("Loaded {} custom command functions", this.g.size());
        }

        this.k.a((Map) this.k.a(iresourcemanager, this.server.aU()).join());
        this.l.addAll(this.k.b(CustomFunctionData.d).a());
        this.m = true;
    }

    @Nullable
    private CustomFunction a(CustomFunction customfunction, @Nullable Throwable throwable, MinecraftKey minecraftkey) {
        if (throwable != null) {
            CustomFunctionData.LOGGER.error("Couldn't load function at {}", minecraftkey, throwable);
            return null;
        } else {
            Map map = this.g;

            synchronized (this.g) {
                this.g.put(customfunction.a(), customfunction);
                return customfunction;
            }
        }
    }

    private static List<String> a(IResourceManager iresourcemanager, MinecraftKey minecraftkey) {
        try {
            IResource iresource = iresourcemanager.a(minecraftkey);
            Throwable throwable = null;

            List list;

            try {
                list = IOUtils.readLines(iresource.b(), StandardCharsets.UTF_8);
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

            return list;
        } catch (IOException ioexception) {
            throw new CompletionException(ioexception);
        }
    }

    public CommandListenerWrapper f() {
        return this.server.getServerCommandListener().a(2).a();
    }

    public CommandListenerWrapper g() {
        return new CommandListenerWrapper(ICommandListener.DUMMY, Vec3D.a, Vec2F.a, (WorldServer) null, this.server.k(), "", new ChatComponentText(""), this.server, (Entity) null);
    }

    public Tags<CustomFunction> h() {
        return this.k;
    }

    public static class a {

        private final CustomFunctionData a;
        private final CommandListenerWrapper b;
        private final CustomFunction.c c;

        public a(CustomFunctionData customfunctiondata, CommandListenerWrapper commandlistenerwrapper, CustomFunction.c customfunction_c) {
            this.a = customfunctiondata;
            this.b = commandlistenerwrapper;
            this.c = customfunction_c;
        }

        public void a(ArrayDeque<CustomFunctionData.a> arraydeque, int i) {
            try {
                this.c.a(this.a, this.b, arraydeque, i);
            } catch (Throwable throwable) {
                ;
            }

        }

        public String toString() {
            return this.c.toString();
        }
    }
}
