package net.minecraft.server;

import com.google.common.primitives.Doubles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;

public class ArgumentParserSelector {

    public static final SimpleCommandExceptionType a = new SimpleCommandExceptionType(new ChatMessage("argument.entity.invalid", new Object[0]));
    public static final DynamicCommandExceptionType b = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("argument.entity.selector.unknown", new Object[] { object});
    });
    public static final SimpleCommandExceptionType c = new SimpleCommandExceptionType(new ChatMessage("argument.entity.selector.not_allowed", new Object[0]));
    public static final SimpleCommandExceptionType d = new SimpleCommandExceptionType(new ChatMessage("argument.entity.selector.missing", new Object[0]));
    public static final SimpleCommandExceptionType e = new SimpleCommandExceptionType(new ChatMessage("argument.entity.options.unterminated", new Object[0]));
    public static final DynamicCommandExceptionType f = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("argument.entity.options.valueless", new Object[] { object});
    });
    public static final BiConsumer<Vec3D, List<? extends Entity>> g = (vec3d, list) -> {
    };
    public static final BiConsumer<Vec3D, List<? extends Entity>> h = (vec3d, list) -> {
        list.sort((entity, entity1) -> {
            return Doubles.compare(entity.a(vec3d), entity1.a(vec3d));
        });
    };
    public static final BiConsumer<Vec3D, List<? extends Entity>> i = (vec3d, list) -> {
        list.sort((entity, entity1) -> {
            return Doubles.compare(entity1.a(vec3d), entity.a(vec3d));
        });
    };
    public static final BiConsumer<Vec3D, List<? extends Entity>> j = (vec3d, list) -> {
        Collections.shuffle(list);
    };
    public static final BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> k = (suggestionsbuilder, consumer) -> {
        return suggestionsbuilder.buildFuture();
    };
    private final StringReader l;
    private final boolean m;
    private int n;
    private boolean o;
    private boolean p;
    private CriterionConditionValue.c q;
    private CriterionConditionValue.d r;
    @Nullable
    private Double s;
    @Nullable
    private Double t;
    @Nullable
    private Double u;
    @Nullable
    private Double v;
    @Nullable
    private Double w;
    @Nullable
    private Double x;
    private CriterionConditionRange y;
    private CriterionConditionRange z;
    private Predicate<Entity> A;
    private BiConsumer<Vec3D, List<? extends Entity>> B;
    private boolean C;
    @Nullable
    private String D;
    private int E;
    @Nullable
    private UUID F;
    private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> G;
    private boolean H;
    private boolean I;
    private boolean J;
    private boolean K;
    private boolean L;
    private boolean M;
    private boolean N;
    private boolean O;
    private Class<? extends Entity> P;
    private boolean Q;
    private boolean R;
    private boolean S;
    private boolean T;

    public ArgumentParserSelector(StringReader stringreader) {
        this(stringreader, true);
    }

    // CraftBukkit start - decompile error
    private static final CriterionConditionValue.c DEFAULT_q;
    private static final CriterionConditionValue.d DEFAULT_r;

    static {
        try {
            DEFAULT_q = (CriterionConditionValue.c) Class.forName("net.minecraft.server.CriterionConditionValue$c").getDeclaredField("e").get(null);
            DEFAULT_r = (CriterionConditionValue.d) Class.forName("net.minecraft.server.CriterionConditionValue$d").getDeclaredField("e").get(null);
        } catch (Exception ex) {
            throw new AssertionError(ex);
        }
    }

    public ArgumentParserSelector(StringReader stringreader, boolean flag) {
        this.q = DEFAULT_q;
        this.r = DEFAULT_r;
        // CraftBukkit end
        this.y = CriterionConditionRange.a;
        this.z = CriterionConditionRange.a;
        this.A = (entity) -> {
            return true;
        };
        this.B = ArgumentParserSelector.g;
        this.G = ArgumentParserSelector.k;
        this.l = stringreader;
        this.m = flag;
    }

    public EntitySelector a() {
        AxisAlignedBB axisalignedbb;

        if (this.v == null && this.w == null && this.x == null) {
            if (this.q.b() != null) {
                float f = (Float) this.q.b();

                axisalignedbb = new AxisAlignedBB((double) (-f), (double) (-f), (double) (-f), (double) (f + 1.0F), (double) (f + 1.0F), (double) (f + 1.0F));
            } else {
                axisalignedbb = null;
            }
        } else {
            axisalignedbb = this.a(this.v == null ? 0.0D : this.v, this.w == null ? 0.0D : this.w, this.x == null ? 0.0D : this.x);
        }

        Function<Vec3D, Vec3D> function; // CraftBukkit - decompile error

        if (this.s == null && this.t == null && this.u == null) {
            function = (vec3d) -> {
                return vec3d;
            };
        } else {
            function = (vec3d) -> {
                return new Vec3D(this.s == null ? vec3d.x : this.s, this.t == null ? vec3d.y : this.t, this.u == null ? vec3d.z : this.u);
            };
        }

        return new EntitySelector(this.n, this.o, this.p, this.A, this.q, function, axisalignedbb, this.B, this.C, this.D, this.F, this.P == null ? Entity.class : this.P, this.T);
    }

    private AxisAlignedBB a(double d0, double d1, double d2) {
        boolean flag = d0 < 0.0D;
        boolean flag1 = d1 < 0.0D;
        boolean flag2 = d2 < 0.0D;
        double d3 = flag ? d0 : 0.0D;
        double d4 = flag1 ? d1 : 0.0D;
        double d5 = flag2 ? d2 : 0.0D;
        double d6 = (flag ? 0.0D : d0) + 1.0D;
        double d7 = (flag1 ? 0.0D : d1) + 1.0D;
        double d8 = (flag2 ? 0.0D : d2) + 1.0D;

        return new AxisAlignedBB(d3, d4, d5, d6, d7, d8);
    }

    private void I() {
        if (this.y != CriterionConditionRange.a) {
            this.A = this.A.and(this.a(this.y, (entity) -> {
                return (double) entity.pitch;
            }));
        }

        if (this.z != CriterionConditionRange.a) {
            this.A = this.A.and(this.a(this.z, (entity) -> {
                return (double) entity.yaw;
            }));
        }

        if (!this.r.c()) {
            this.A = this.A.and((entity) -> {
                return !(entity instanceof EntityPlayer) ? false : this.r.d(((EntityPlayer) entity).expLevel);
            });
        }

    }

    private Predicate<Entity> a(CriterionConditionRange criterionconditionrange, ToDoubleFunction<Entity> todoublefunction) {
        double d0 = (double) MathHelper.g(criterionconditionrange.a() == null ? 0.0F : criterionconditionrange.a());
        double d1 = (double) MathHelper.g(criterionconditionrange.b() == null ? 359.0F : criterionconditionrange.b());

        return (entity) -> {
            double d2 = MathHelper.g(todoublefunction.applyAsDouble(entity));

            return d0 > d1 ? d2 >= d0 || d2 <= d1 : d2 >= d0 && d2 <= d1;
        };
    }

    // CraftBukkit start
    protected void b(boolean overridePermissions) throws CommandSyntaxException {
        this.T = !overridePermissions;
        // CraftBukkit end
        this.G = this::d;
        if (!this.l.canRead()) {
            throw ArgumentParserSelector.d.createWithContext(this.l);
        } else {
            int i = this.l.getCursor();
            char c0 = this.l.read();

            if (c0 == 'p') {
                this.n = 1;
                this.o = false;
                this.B = ArgumentParserSelector.h;
                this.a(EntityPlayer.class);
            } else if (c0 == 'a') {
                this.n = Integer.MAX_VALUE;
                this.o = false;
                this.B = ArgumentParserSelector.g;
                this.a(EntityPlayer.class);
            } else if (c0 == 'r') {
                this.n = 1;
                this.o = false;
                this.B = ArgumentParserSelector.j;
                this.a(EntityPlayer.class);
            } else if (c0 == 's') {
                this.n = 1;
                this.o = true;
                this.C = true;
            } else {
                if (c0 != 'e') {
                    this.l.setCursor(i);
                    throw ArgumentParserSelector.b.createWithContext(this.l, '@' + String.valueOf(c0));
                }

                this.n = Integer.MAX_VALUE;
                this.o = true;
                this.B = ArgumentParserSelector.g;
                this.A = Entity::isAlive;
            }

            this.G = this::e;
            if (this.l.canRead() && this.l.peek() == '[') {
                this.l.skip();
                this.G = this::f;
                this.d();
            }

        }
    }

    protected void c() throws CommandSyntaxException {
        if (this.l.canRead()) {
            this.G = this::c;
        }

        int i = this.l.getCursor();
        String s = this.l.readString();

        try {
            this.F = UUID.fromString(s);
            this.o = true;
        } catch (IllegalArgumentException illegalargumentexception) {
            if (s.isEmpty() || s.length() > 16) {
                this.l.setCursor(i);
                throw ArgumentParserSelector.a.createWithContext(this.l);
            }

            this.o = false;
            this.D = s;
        }

        this.n = 1;
    }

    protected void d() throws CommandSyntaxException {
        this.G = this::g;
        this.l.skipWhitespace();

        while (true) {
            if (this.l.canRead() && this.l.peek() != ']') {
                this.l.skipWhitespace();
                int i = this.l.getCursor();
                String s = this.l.readString();
                PlayerSelector.a playerselector_a = PlayerSelector.a(this, s, i);

                this.l.skipWhitespace();
                if (!this.l.canRead() || this.l.peek() != '=') {
                    this.l.setCursor(i);
                    throw ArgumentParserSelector.f.createWithContext(this.l, s);
                }

                this.l.skip();
                this.l.skipWhitespace();
                this.G = ArgumentParserSelector.k;
                playerselector_a.handle(this);
                this.l.skipWhitespace();
                this.G = this::h;
                if (!this.l.canRead()) {
                    continue;
                }

                if (this.l.peek() == ',') {
                    this.l.skip();
                    this.G = this::g;
                    continue;
                }

                if (this.l.peek() != ']') {
                    throw ArgumentParserSelector.e.createWithContext(this.l);
                }
            }

            if (this.l.canRead()) {
                this.l.skip();
                this.G = ArgumentParserSelector.k;
                return;
            }

            throw ArgumentParserSelector.e.createWithContext(this.l);
        }
    }

    public boolean e() {
        this.l.skipWhitespace();
        if (this.l.canRead() && this.l.peek() == '!') {
            this.l.skip();
            this.l.skipWhitespace();
            return true;
        } else {
            return false;
        }
    }

    public StringReader f() {
        return this.l;
    }

    public void a(Predicate<Entity> predicate) {
        this.A = this.A.and(predicate);
    }

    public void g() {
        this.p = true;
    }

    public CriterionConditionValue.c h() {
        return this.q;
    }

    public void a(CriterionConditionValue.c criterionconditionvalue_c) {
        this.q = criterionconditionvalue_c;
    }

    public CriterionConditionValue.d i() {
        return this.r;
    }

    public void a(CriterionConditionValue.d criterionconditionvalue_d) {
        this.r = criterionconditionvalue_d;
    }

    public CriterionConditionRange j() {
        return this.y;
    }

    public void a(CriterionConditionRange criterionconditionrange) {
        this.y = criterionconditionrange;
    }

    public CriterionConditionRange k() {
        return this.z;
    }

    public void b(CriterionConditionRange criterionconditionrange) {
        this.z = criterionconditionrange;
    }

    @Nullable
    public Double l() {
        return this.s;
    }

    @Nullable
    public Double m() {
        return this.t;
    }

    @Nullable
    public Double n() {
        return this.u;
    }

    public void a(double d0) {
        this.s = d0;
    }

    public void b(double d0) {
        this.t = d0;
    }

    public void c(double d0) {
        this.u = d0;
    }

    public void d(double d0) {
        this.v = d0;
    }

    public void e(double d0) {
        this.w = d0;
    }

    public void f(double d0) {
        this.x = d0;
    }

    @Nullable
    public Double o() {
        return this.v;
    }

    @Nullable
    public Double p() {
        return this.w;
    }

    @Nullable
    public Double q() {
        return this.x;
    }

    public void a(int i) {
        this.n = i;
    }

    public void a(boolean flag) {
        this.o = flag;
    }

    public void a(BiConsumer<Vec3D, List<? extends Entity>> biconsumer) {
        this.B = biconsumer;
    }

    public EntitySelector s() throws CommandSyntaxException {
        // CraftBukkit start
        return s(false);
    }

    public EntitySelector s(boolean overridePermissions) throws CommandSyntaxException {
        // CraftBukkit end
        this.E = this.l.getCursor();
        this.G = this::b;
        if (this.l.canRead() && this.l.peek() == '@') {
            if (!this.m) {
                throw ArgumentParserSelector.c.createWithContext(this.l);
            }

            this.l.skip();
            this.b(overridePermissions); // CraftBukkit
        } else {
            this.c();
        }

        this.I();
        return this.a();
    }

    private static void a(SuggestionsBuilder suggestionsbuilder) {
        suggestionsbuilder.suggest("@p", new ChatMessage("argument.entity.selector.nearestPlayer", new Object[0]));
        suggestionsbuilder.suggest("@a", new ChatMessage("argument.entity.selector.allPlayers", new Object[0]));
        suggestionsbuilder.suggest("@r", new ChatMessage("argument.entity.selector.randomPlayer", new Object[0]));
        suggestionsbuilder.suggest("@s", new ChatMessage("argument.entity.selector.self", new Object[0]));
        suggestionsbuilder.suggest("@e", new ChatMessage("argument.entity.selector.allEntities", new Object[0]));
    }

    private CompletableFuture<Suggestions> b(SuggestionsBuilder suggestionsbuilder, Consumer<SuggestionsBuilder> consumer) {
        consumer.accept(suggestionsbuilder);
        if (this.m) {
            a(suggestionsbuilder);
        }

        return suggestionsbuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> c(SuggestionsBuilder suggestionsbuilder, Consumer<SuggestionsBuilder> consumer) {
        SuggestionsBuilder suggestionsbuilder1 = suggestionsbuilder.createOffset(this.E);

        consumer.accept(suggestionsbuilder1);
        return suggestionsbuilder.add(suggestionsbuilder1).buildFuture();
    }

    private CompletableFuture<Suggestions> d(SuggestionsBuilder suggestionsbuilder, Consumer<SuggestionsBuilder> consumer) {
        SuggestionsBuilder suggestionsbuilder1 = suggestionsbuilder.createOffset(suggestionsbuilder.getStart() - 1);

        a(suggestionsbuilder1);
        suggestionsbuilder.add(suggestionsbuilder1);
        return suggestionsbuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> e(SuggestionsBuilder suggestionsbuilder, Consumer<SuggestionsBuilder> consumer) {
        suggestionsbuilder.suggest(String.valueOf('['));
        return suggestionsbuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> f(SuggestionsBuilder suggestionsbuilder, Consumer<SuggestionsBuilder> consumer) {
        suggestionsbuilder.suggest(String.valueOf(']'));
        PlayerSelector.a(this, suggestionsbuilder);
        return suggestionsbuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> g(SuggestionsBuilder suggestionsbuilder, Consumer<SuggestionsBuilder> consumer) {
        PlayerSelector.a(this, suggestionsbuilder);
        return suggestionsbuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> h(SuggestionsBuilder suggestionsbuilder, Consumer<SuggestionsBuilder> consumer) {
        suggestionsbuilder.suggest(String.valueOf(','));
        suggestionsbuilder.suggest(String.valueOf(']'));
        return suggestionsbuilder.buildFuture();
    }

    public boolean t() {
        return this.C;
    }

    public void a(BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> bifunction) {
        this.G = bifunction;
    }

    public CompletableFuture<Suggestions> a(SuggestionsBuilder suggestionsbuilder, Consumer<SuggestionsBuilder> consumer) {
        return (CompletableFuture) this.G.apply(suggestionsbuilder.createOffset(this.l.getCursor()), consumer);
    }

    public boolean u() {
        return this.H;
    }

    public void c(boolean flag) {
        this.H = flag;
    }

    public boolean v() {
        return this.I;
    }

    public void d(boolean flag) {
        this.I = flag;
    }

    public boolean w() {
        return this.J;
    }

    public void e(boolean flag) {
        this.J = flag;
    }

    public boolean x() {
        return this.K;
    }

    public void f(boolean flag) {
        this.K = flag;
    }

    public boolean y() {
        return this.L;
    }

    public void g(boolean flag) {
        this.L = flag;
    }

    public boolean z() {
        return this.M;
    }

    public void h(boolean flag) {
        this.M = flag;
    }

    public boolean A() {
        return this.N;
    }

    public void i(boolean flag) {
        this.N = flag;
    }

    public void j(boolean flag) {
        this.O = flag;
    }

    public void a(Class<? extends Entity> oclass) {
        this.P = oclass;
    }

    public void C() {
        this.Q = true;
    }

    public boolean E() {
        return this.P != null;
    }

    public boolean F() {
        return this.Q;
    }

    public boolean G() {
        return this.R;
    }

    public void k(boolean flag) {
        this.R = flag;
    }

    public boolean H() {
        return this.S;
    }

    public void l(boolean flag) {
        this.S = flag;
    }
}
