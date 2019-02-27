package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;

public class ArgumentBlock {

    public static final SimpleCommandExceptionType a = new SimpleCommandExceptionType(new ChatMessage("argument.block.tag.disallowed", new Object[0]));
    public static final DynamicCommandExceptionType b = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("argument.block.id.invalid", new Object[] { object});
    });
    public static final Dynamic2CommandExceptionType c = new Dynamic2CommandExceptionType((object, object1) -> {
        return new ChatMessage("argument.block.property.unknown", new Object[] { object, object1});
    });
    public static final Dynamic2CommandExceptionType d = new Dynamic2CommandExceptionType((object, object1) -> {
        return new ChatMessage("argument.block.property.duplicate", new Object[] { object1, object});
    });
    public static final Dynamic3CommandExceptionType e = new Dynamic3CommandExceptionType((object, object1, object2) -> {
        return new ChatMessage("argument.block.property.invalid", new Object[] { object, object2, object1});
    });
    public static final Dynamic2CommandExceptionType f = new Dynamic2CommandExceptionType((object, object1) -> {
        return new ChatMessage("argument.block.property.novalue", new Object[] { object, object1});
    });
    public static final SimpleCommandExceptionType g = new SimpleCommandExceptionType(new ChatMessage("argument.block.property.unclosed", new Object[0]));
    private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> h = SuggestionsBuilder::buildFuture;
    private final StringReader i;
    private final boolean j;
    private final Map<IBlockState<?>, Comparable<?>> k = Maps.newHashMap();
    private final Map<String, String> l = Maps.newHashMap();
    private MinecraftKey m = new MinecraftKey("");
    private BlockStateList<Block, IBlockData> n;
    private IBlockData o;
    @Nullable
    private NBTTagCompound p;
    private MinecraftKey q = new MinecraftKey("");
    private int r;
    private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> s;

    public ArgumentBlock(StringReader stringreader, boolean flag) {
        this.s = ArgumentBlock.h;
        this.i = stringreader;
        this.j = flag;
    }

    public Map<IBlockState<?>, Comparable<?>> getStateMap() {
        return this.k;
    }

    @Nullable
    public IBlockData getBlockData() {
        return this.o;
    }

    @Nullable
    public NBTTagCompound c() {
        return this.p;
    }

    @Nullable
    public MinecraftKey d() {
        return this.q;
    }

    public ArgumentBlock a(boolean flag) throws CommandSyntaxException {
        this.s = this::l;
        if (this.i.canRead() && this.i.peek() == '#') {
            this.f();
            this.s = this::i;
            if (this.i.canRead() && this.i.peek() == '[') {
                this.h();
                this.s = this::f;
            }
        } else {
            this.e();
            this.s = this::j;
            if (this.i.canRead() && this.i.peek() == '[') {
                this.g();
                this.s = this::f;
            }
        }

        if (flag && this.i.canRead() && this.i.peek() == '{') {
            this.s = ArgumentBlock.h;
            this.i();
        }

        return this;
    }

    private CompletableFuture<Suggestions> b(SuggestionsBuilder suggestionsbuilder) {
        if (suggestionsbuilder.getRemaining().isEmpty()) {
            suggestionsbuilder.suggest(String.valueOf(']'));
        }

        return this.d(suggestionsbuilder);
    }

    private CompletableFuture<Suggestions> c(SuggestionsBuilder suggestionsbuilder) {
        if (suggestionsbuilder.getRemaining().isEmpty()) {
            suggestionsbuilder.suggest(String.valueOf(']'));
        }

        return this.e(suggestionsbuilder);
    }

    private CompletableFuture<Suggestions> d(SuggestionsBuilder suggestionsbuilder) {
        String s = suggestionsbuilder.getRemaining().toLowerCase(Locale.ROOT);
        Iterator iterator = this.o.a().iterator();

        while (iterator.hasNext()) {
            IBlockState<?> iblockstate = (IBlockState) iterator.next();

            if (!this.k.containsKey(iblockstate) && iblockstate.a().startsWith(s)) {
                suggestionsbuilder.suggest(iblockstate.a() + '=');
            }
        }

        return suggestionsbuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> e(SuggestionsBuilder suggestionsbuilder) {
        String s = suggestionsbuilder.getRemaining().toLowerCase(Locale.ROOT);

        if (this.q != null && !this.q.getKey().isEmpty()) {
            Tag<Block> tag = TagsBlock.a().a(this.q);

            if (tag != null) {
                Iterator iterator = tag.a().iterator();

                while (iterator.hasNext()) {
                    Block block = (Block) iterator.next();
                    Iterator iterator1 = block.getStates().d().iterator();

                    while (iterator1.hasNext()) {
                        IBlockState<?> iblockstate = (IBlockState) iterator1.next();

                        if (!this.l.containsKey(iblockstate.a()) && iblockstate.a().startsWith(s)) {
                            suggestionsbuilder.suggest(iblockstate.a() + '=');
                        }
                    }
                }
            }
        }

        return suggestionsbuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> f(SuggestionsBuilder suggestionsbuilder) {
        if (suggestionsbuilder.getRemaining().isEmpty() && this.k()) {
            suggestionsbuilder.suggest(String.valueOf('{'));
        }

        return suggestionsbuilder.buildFuture();
    }

    private boolean k() {
        if (this.o != null) {
            return this.o.getBlock().isTileEntity();
        } else {
            if (this.q != null) {
                Tag<Block> tag = TagsBlock.a().a(this.q);

                if (tag != null) {
                    Iterator iterator = tag.a().iterator();

                    while (iterator.hasNext()) {
                        Block block = (Block) iterator.next();

                        if (block.isTileEntity()) {
                            return true;
                        }
                    }
                }
            }

            return false;
        }
    }

    private CompletableFuture<Suggestions> g(SuggestionsBuilder suggestionsbuilder) {
        if (suggestionsbuilder.getRemaining().isEmpty()) {
            suggestionsbuilder.suggest(String.valueOf('='));
        }

        return suggestionsbuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> h(SuggestionsBuilder suggestionsbuilder) {
        if (suggestionsbuilder.getRemaining().isEmpty()) {
            suggestionsbuilder.suggest(String.valueOf(']'));
        }

        if (suggestionsbuilder.getRemaining().isEmpty() && this.k.size() < this.o.a().size()) {
            suggestionsbuilder.suggest(String.valueOf(','));
        }

        return suggestionsbuilder.buildFuture();
    }

    private static <T extends Comparable<T>> SuggestionsBuilder a(SuggestionsBuilder suggestionsbuilder, IBlockState<T> iblockstate) {
        Iterator iterator = iblockstate.d().iterator();

        while (iterator.hasNext()) {
            T t0 = (Comparable) iterator.next();

            if (t0 instanceof Integer) {
                suggestionsbuilder.suggest((Integer) t0);
            } else {
                suggestionsbuilder.suggest(iblockstate.a(t0));
            }
        }

        return suggestionsbuilder;
    }

    private CompletableFuture<Suggestions> a(SuggestionsBuilder suggestionsbuilder, String s) {
        boolean flag = false;

        if (this.q != null && !this.q.getKey().isEmpty()) {
            Tag<Block> tag = TagsBlock.a().a(this.q);

            if (tag != null) {
                Iterator iterator = tag.a().iterator();

                while (iterator.hasNext()) {
                    Block block = (Block) iterator.next();
                    IBlockState<?> iblockstate = block.getStates().a(s);

                    if (iblockstate != null) {
                        a(suggestionsbuilder, iblockstate);
                    }

                    if (!flag) {
                        Iterator iterator1 = block.getStates().d().iterator();

                        while (iterator1.hasNext()) {
                            IBlockState<?> iblockstate1 = (IBlockState) iterator1.next();

                            if (!this.l.containsKey(iblockstate1.a())) {
                                flag = true;
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (flag) {
            suggestionsbuilder.suggest(String.valueOf(','));
        }

        suggestionsbuilder.suggest(String.valueOf(']'));
        return suggestionsbuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> i(SuggestionsBuilder suggestionsbuilder) {
        if (suggestionsbuilder.getRemaining().isEmpty()) {
            Tag<Block> tag = TagsBlock.a().a(this.q);

            if (tag != null) {
                boolean flag = false;
                boolean flag1 = false;
                Iterator iterator = tag.a().iterator();

                while (iterator.hasNext()) {
                    Block block = (Block) iterator.next();

                    flag |= !block.getStates().d().isEmpty();
                    flag1 |= block.isTileEntity();
                    if (flag && flag1) {
                        break;
                    }
                }

                if (flag) {
                    suggestionsbuilder.suggest(String.valueOf('['));
                }

                if (flag1) {
                    suggestionsbuilder.suggest(String.valueOf('{'));
                }
            }
        }

        return this.k(suggestionsbuilder);
    }

    private CompletableFuture<Suggestions> j(SuggestionsBuilder suggestionsbuilder) {
        if (suggestionsbuilder.getRemaining().isEmpty()) {
            if (!this.o.getBlock().getStates().d().isEmpty()) {
                suggestionsbuilder.suggest(String.valueOf('['));
            }

            if (this.o.getBlock().isTileEntity()) {
                suggestionsbuilder.suggest(String.valueOf('{'));
            }
        }

        return suggestionsbuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> k(SuggestionsBuilder suggestionsbuilder) {
        return ICompletionProvider.a((Iterable) TagsBlock.a().a(), suggestionsbuilder.createOffset(this.r).add(suggestionsbuilder));
    }

    private CompletableFuture<Suggestions> l(SuggestionsBuilder suggestionsbuilder) {
        if (this.j) {
            ICompletionProvider.a((Iterable) TagsBlock.a().a(), suggestionsbuilder, String.valueOf('#'));
        }

        ICompletionProvider.a((Iterable) IRegistry.BLOCK.keySet(), suggestionsbuilder);
        return suggestionsbuilder.buildFuture();
    }

    public void e() throws CommandSyntaxException {
        int i = this.i.getCursor();

        this.m = MinecraftKey.a(this.i);
        if (IRegistry.BLOCK.c(this.m)) {
            Block block = (Block) IRegistry.BLOCK.getOrDefault(this.m);

            this.n = block.getStates();
            this.o = block.getBlockData();
        } else {
            this.i.setCursor(i);
            throw ArgumentBlock.b.createWithContext(this.i, this.m.toString());
        }
    }

    public void f() throws CommandSyntaxException {
        if (!this.j) {
            throw ArgumentBlock.a.create();
        } else {
            this.s = this::k;
            this.i.expect('#');
            this.r = this.i.getCursor();
            this.q = MinecraftKey.a(this.i);
        }
    }

    public void g() throws CommandSyntaxException {
        this.i.skip();
        this.s = this::b;
        this.i.skipWhitespace();

        while (true) {
            if (this.i.canRead() && this.i.peek() != ']') {
                this.i.skipWhitespace();
                int i = this.i.getCursor();
                String s = this.i.readString();
                IBlockState<?> iblockstate = this.n.a(s);

                if (iblockstate == null) {
                    this.i.setCursor(i);
                    throw ArgumentBlock.c.createWithContext(this.i, this.m.toString(), s);
                }

                if (this.k.containsKey(iblockstate)) {
                    this.i.setCursor(i);
                    throw ArgumentBlock.d.createWithContext(this.i, this.m.toString(), s);
                }

                this.i.skipWhitespace();
                this.s = this::g;
                if (!this.i.canRead() || this.i.peek() != '=') {
                    throw ArgumentBlock.f.createWithContext(this.i, this.m.toString(), s);
                }

                this.i.skip();
                this.i.skipWhitespace();
                this.s = (suggestionsbuilder) -> {
                    return a(suggestionsbuilder, iblockstate).buildFuture();
                };
                int j = this.i.getCursor();

                this.a(iblockstate, this.i.readString(), j);
                this.s = this::h;
                this.i.skipWhitespace();
                if (!this.i.canRead()) {
                    continue;
                }

                if (this.i.peek() == ',') {
                    this.i.skip();
                    this.s = this::d;
                    continue;
                }

                if (this.i.peek() != ']') {
                    throw ArgumentBlock.g.createWithContext(this.i);
                }
            }

            if (this.i.canRead()) {
                this.i.skip();
                return;
            }

            throw ArgumentBlock.g.createWithContext(this.i);
        }
    }

    public void h() throws CommandSyntaxException {
        this.i.skip();
        this.s = this::c;
        int i = -1;

        this.i.skipWhitespace();

        while (true) {
            if (this.i.canRead() && this.i.peek() != ']') {
                this.i.skipWhitespace();
                int j = this.i.getCursor();
                String s = this.i.readString();

                if (this.l.containsKey(s)) {
                    this.i.setCursor(j);
                    throw ArgumentBlock.d.createWithContext(this.i, this.m.toString(), s);
                }

                this.i.skipWhitespace();
                if (!this.i.canRead() || this.i.peek() != '=') {
                    this.i.setCursor(j);
                    throw ArgumentBlock.f.createWithContext(this.i, this.m.toString(), s);
                }

                this.i.skip();
                this.i.skipWhitespace();
                this.s = (suggestionsbuilder) -> {
                    return this.a(suggestionsbuilder, s);
                };
                i = this.i.getCursor();
                String s1 = this.i.readString();

                this.l.put(s, s1);
                this.i.skipWhitespace();
                if (!this.i.canRead()) {
                    continue;
                }

                i = -1;
                if (this.i.peek() == ',') {
                    this.i.skip();
                    this.s = this::e;
                    continue;
                }

                if (this.i.peek() != ']') {
                    throw ArgumentBlock.g.createWithContext(this.i);
                }
            }

            if (this.i.canRead()) {
                this.i.skip();
                return;
            }

            if (i >= 0) {
                this.i.setCursor(i);
            }

            throw ArgumentBlock.g.createWithContext(this.i);
        }
    }

    public void i() throws CommandSyntaxException {
        this.p = (new MojangsonParser(this.i)).f();
    }

    private <T extends Comparable<T>> void a(IBlockState<T> iblockstate, String s, int i) throws CommandSyntaxException {
        Optional<T> optional = iblockstate.b(s);

        if (optional.isPresent()) {
            this.o = (IBlockData) this.o.set(iblockstate, (Comparable) optional.get());
            this.k.put(iblockstate, optional.get());
        } else {
            this.i.setCursor(i);
            throw ArgumentBlock.e.createWithContext(this.i, this.m.toString(), iblockstate.a(), s);
        }
    }

    public static String a(IBlockData iblockdata, @Nullable NBTTagCompound nbttagcompound) {
        StringBuilder stringbuilder = new StringBuilder(IRegistry.BLOCK.getKey(iblockdata.getBlock()).toString());

        if (!iblockdata.a().isEmpty()) {
            stringbuilder.append('[');
            boolean flag = false;

            for (UnmodifiableIterator unmodifiableiterator = iblockdata.getStateMap().entrySet().iterator(); unmodifiableiterator.hasNext(); flag = true) {
                Entry<IBlockState<?>, Comparable<?>> entry = (Entry) unmodifiableiterator.next();

                if (flag) {
                    stringbuilder.append(',');
                }

                a(stringbuilder, (IBlockState) entry.getKey(), (Comparable) entry.getValue());
            }

            stringbuilder.append(']');
        }

        if (nbttagcompound != null) {
            stringbuilder.append(nbttagcompound);
        }

        return stringbuilder.toString();
    }

    private static <T extends Comparable<T>> void a(StringBuilder stringbuilder, IBlockState<T> iblockstate, Comparable<?> comparable) {
        stringbuilder.append(iblockstate.a());
        stringbuilder.append('=');
        stringbuilder.append(iblockstate.a(comparable));
    }

    public CompletableFuture<Suggestions> a(SuggestionsBuilder suggestionsbuilder) {
        return (CompletableFuture) this.s.apply(suggestionsbuilder.createOffset(this.i.getCursor()));
    }

    public Map<String, String> j() {
        return this.l;
    }
}
