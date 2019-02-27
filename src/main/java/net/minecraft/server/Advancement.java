package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.apache.commons.lang3.ArrayUtils;

public class Advancement {

    private final Advancement a;
    private final AdvancementDisplay b;
    private final AdvancementRewards c;
    private final MinecraftKey d;
    private final Map<String, Criterion> e;
    private final String[][] f;
    private final Set<Advancement> g = Sets.newLinkedHashSet();
    private final IChatBaseComponent h;
    public final org.bukkit.advancement.Advancement bukkit = new org.bukkit.craftbukkit.advancement.CraftAdvancement(this); // CraftBukkit

    public Advancement(MinecraftKey minecraftkey, @Nullable Advancement advancement, @Nullable AdvancementDisplay advancementdisplay, AdvancementRewards advancementrewards, Map<String, Criterion> map, String[][] astring) {
        this.d = minecraftkey;
        this.b = advancementdisplay;
        this.e = ImmutableMap.copyOf(map);
        this.a = advancement;
        this.c = advancementrewards;
        this.f = astring;
        if (advancement != null) {
            advancement.a(this);
        }

        if (advancementdisplay == null) {
            this.h = new ChatComponentText(minecraftkey.toString());
        } else {
            IChatBaseComponent ichatbasecomponent = advancementdisplay.a();
            EnumChatFormat enumchatformat = advancementdisplay.e().c();
            IChatBaseComponent ichatbasecomponent1 = ichatbasecomponent.h().a(enumchatformat).a("\n").addSibling(advancementdisplay.b());
            IChatBaseComponent ichatbasecomponent2 = ichatbasecomponent.h().a((chatmodifier) -> {
                chatmodifier.setChatHoverable(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, ichatbasecomponent1));
            });

            this.h = (new ChatComponentText("[")).addSibling(ichatbasecomponent2).a("]").a(enumchatformat);
        }

    }

    public Advancement.SerializedAdvancement a() {
        return new Advancement.SerializedAdvancement(this.a == null ? null : this.a.getName(), this.b, this.c, this.e, this.f);
    }

    @Nullable
    public Advancement b() {
        return this.a;
    }

    @Nullable
    public AdvancementDisplay c() {
        return this.b;
    }

    public AdvancementRewards d() {
        return this.c;
    }

    public String toString() {
        return "SimpleAdvancement{id=" + this.getName() + ", parent=" + (this.a == null ? "null" : this.a.getName()) + ", display=" + this.b + ", rewards=" + this.c + ", criteria=" + this.e + ", requirements=" + Arrays.deepToString(this.f) + '}';
    }

    public Iterable<Advancement> e() {
        return this.g;
    }

    public Map<String, Criterion> getCriteria() {
        return this.e;
    }

    public void a(Advancement advancement) {
        this.g.add(advancement);
    }

    public MinecraftKey getName() {
        return this.d;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof Advancement)) {
            return false;
        } else {
            Advancement advancement = (Advancement) object;

            return this.d.equals(advancement.d);
        }
    }

    public int hashCode() {
        return this.d.hashCode();
    }

    public String[][] i() {
        return this.f;
    }

    public IChatBaseComponent j() {
        return this.h;
    }

    public static class SerializedAdvancement {

        private MinecraftKey a;
        private Advancement b;
        private AdvancementDisplay c;
        private AdvancementRewards d;
        private Map<String, Criterion> e;
        private String[][] f;
        private AdvancementRequirements g;

        private SerializedAdvancement(@Nullable MinecraftKey minecraftkey, @Nullable AdvancementDisplay advancementdisplay, AdvancementRewards advancementrewards, Map<String, Criterion> map, String[][] astring) {
            this.d = AdvancementRewards.a;
            this.e = Maps.newLinkedHashMap();
            this.g = AdvancementRequirements.AND;
            this.a = minecraftkey;
            this.c = advancementdisplay;
            this.d = advancementrewards;
            this.e = map;
            this.f = astring;
        }

        private SerializedAdvancement() {
            this.d = AdvancementRewards.a;
            this.e = Maps.newLinkedHashMap();
            this.g = AdvancementRequirements.AND;
        }

        public static Advancement.SerializedAdvancement a() {
            return new Advancement.SerializedAdvancement();
        }

        public Advancement.SerializedAdvancement a(Advancement advancement) {
            this.b = advancement;
            return this;
        }

        public Advancement.SerializedAdvancement a(MinecraftKey minecraftkey) {
            this.a = minecraftkey;
            return this;
        }

        public Advancement.SerializedAdvancement a(IMaterial imaterial, IChatBaseComponent ichatbasecomponent, IChatBaseComponent ichatbasecomponent1, @Nullable MinecraftKey minecraftkey, AdvancementFrameType advancementframetype, boolean flag, boolean flag1, boolean flag2) {
            return this.a(new AdvancementDisplay(new ItemStack(imaterial.getItem()), ichatbasecomponent, ichatbasecomponent1, minecraftkey, advancementframetype, flag, flag1, flag2));
        }

        public Advancement.SerializedAdvancement a(AdvancementDisplay advancementdisplay) {
            this.c = advancementdisplay;
            return this;
        }

        public Advancement.SerializedAdvancement a(AdvancementRewards.a advancementrewards_a) {
            return this.a(advancementrewards_a.a());
        }

        public Advancement.SerializedAdvancement a(AdvancementRewards advancementrewards) {
            this.d = advancementrewards;
            return this;
        }

        public Advancement.SerializedAdvancement a(String s, CriterionInstance criterioninstance) {
            return this.a(s, new Criterion(criterioninstance));
        }

        public Advancement.SerializedAdvancement a(String s, Criterion criterion) {
            if (this.e.containsKey(s)) {
                throw new IllegalArgumentException("Duplicate criterion " + s);
            } else {
                this.e.put(s, criterion);
                return this;
            }
        }

        public Advancement.SerializedAdvancement a(AdvancementRequirements advancementrequirements) {
            this.g = advancementrequirements;
            return this;
        }

        public boolean a(Function<MinecraftKey, Advancement> function) {
            if (this.a == null) {
                return true;
            } else {
                if (this.b == null) {
                    this.b = (Advancement) function.apply(this.a);
                }

                return this.b != null;
            }
        }

        public Advancement b(MinecraftKey minecraftkey) {
            if (!this.a((Function<MinecraftKey, Advancement>) (minecraftkey1) -> { // CraftBukkit - decompile error
                return null;
            })) {
                throw new IllegalStateException("Tried to build incomplete advancement!");
            } else {
                if (this.f == null) {
                    this.f = this.g.createRequirements(this.e.keySet());
                }

                return new Advancement(minecraftkey, this.b, this.c, this.d, this.e, this.f);
            }
        }

        public Advancement a(Consumer<Advancement> consumer, String s) {
            Advancement advancement = this.b(new MinecraftKey(s));

            consumer.accept(advancement);
            return advancement;
        }

        public JsonObject b() {
            if (this.f == null) {
                this.f = this.g.createRequirements(this.e.keySet());
            }

            JsonObject jsonobject = new JsonObject();

            if (this.b != null) {
                jsonobject.addProperty("parent", this.b.getName().toString());
            } else if (this.a != null) {
                jsonobject.addProperty("parent", this.a.toString());
            }

            if (this.c != null) {
                jsonobject.add("display", this.c.k());
            }

            jsonobject.add("rewards", this.d.b());
            JsonObject jsonobject1 = new JsonObject();
            Iterator iterator = this.e.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<String, Criterion> entry = (Entry) iterator.next();

                jsonobject1.add((String) entry.getKey(), ((Criterion) entry.getValue()).b());
            }

            jsonobject.add("criteria", jsonobject1);
            JsonArray jsonarray = new JsonArray();
            String[][] astring = this.f;
            int i = astring.length;

            for (int j = 0; j < i; ++j) {
                String[] astring1 = astring[j];
                JsonArray jsonarray1 = new JsonArray();
                String[] astring2 = astring1;
                int k = astring1.length;

                for (int l = 0; l < k; ++l) {
                    String s = astring2[l];

                    jsonarray1.add(s);
                }

                jsonarray.add(jsonarray1);
            }

            jsonobject.add("requirements", jsonarray);
            return jsonobject;
        }

        public void a(PacketDataSerializer packetdataserializer) {
            if (this.a == null) {
                packetdataserializer.writeBoolean(false);
            } else {
                packetdataserializer.writeBoolean(true);
                packetdataserializer.a(this.a);
            }

            if (this.c == null) {
                packetdataserializer.writeBoolean(false);
            } else {
                packetdataserializer.writeBoolean(true);
                this.c.a(packetdataserializer);
            }

            Criterion.a(this.e, packetdataserializer);
            packetdataserializer.d(this.f.length);
            String[][] astring = this.f;
            int i = astring.length;

            for (int j = 0; j < i; ++j) {
                String[] astring1 = astring[j];

                packetdataserializer.d(astring1.length);
                String[] astring2 = astring1;
                int k = astring1.length;

                for (int l = 0; l < k; ++l) {
                    String s = astring2[l];

                    packetdataserializer.a(s);
                }
            }

        }

        public String toString() {
            return "Task Advancement{parentId=" + this.a + ", display=" + this.c + ", rewards=" + this.d + ", criteria=" + this.e + ", requirements=" + Arrays.deepToString(this.f) + '}';
        }

        public static Advancement.SerializedAdvancement a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            MinecraftKey minecraftkey = jsonobject.has("parent") ? new MinecraftKey(ChatDeserializer.h(jsonobject, "parent")) : null;
            AdvancementDisplay advancementdisplay = jsonobject.has("display") ? AdvancementDisplay.a(ChatDeserializer.t(jsonobject, "display"), jsondeserializationcontext) : null;
            AdvancementRewards advancementrewards = (AdvancementRewards) ChatDeserializer.a(jsonobject, "rewards", AdvancementRewards.a, jsondeserializationcontext, AdvancementRewards.class);
            Map<String, Criterion> map = Criterion.b(ChatDeserializer.t(jsonobject, "criteria"), jsondeserializationcontext);

            if (map.isEmpty()) {
                throw new JsonSyntaxException("Advancement criteria cannot be empty");
            } else {
                JsonArray jsonarray = ChatDeserializer.a(jsonobject, "requirements", new JsonArray());
                String[][] astring = new String[jsonarray.size()][];

                int i;
                int j;

                for (i = 0; i < jsonarray.size(); ++i) {
                    JsonArray jsonarray1 = ChatDeserializer.n(jsonarray.get(i), "requirements[" + i + "]");

                    astring[i] = new String[jsonarray1.size()];

                    for (j = 0; j < jsonarray1.size(); ++j) {
                        astring[i][j] = ChatDeserializer.a(jsonarray1.get(j), "requirements[" + i + "][" + j + "]");
                    }
                }

                if (astring.length == 0) {
                    astring = new String[map.size()][];
                    i = 0;

                    String s;

                    for (Iterator iterator = map.keySet().iterator(); iterator.hasNext(); astring[i++] = new String[] { s}) {
                        s = (String) iterator.next();
                    }
                }

                String[][] astring1 = astring;
                int k = astring.length;

                int l;

                for (j = 0; j < k; ++j) {
                    String[] astring2 = astring1[j];

                    if (astring2.length == 0 && map.isEmpty()) {
                        throw new JsonSyntaxException("Requirement entry cannot be empty");
                    }

                    String[] astring3 = astring2;

                    l = astring2.length;

                    for (int i1 = 0; i1 < l; ++i1) {
                        String s1 = astring3[i1];

                        if (!map.containsKey(s1)) {
                            throw new JsonSyntaxException("Unknown required criterion '" + s1 + "'");
                        }
                    }
                }

                Iterator iterator1 = map.keySet().iterator();

                while (iterator1.hasNext()) {
                    String s2 = (String) iterator1.next();
                    boolean flag = false;
                    String[][] astring4 = astring;
                    int j1 = astring.length;

                    l = 0;

                    while (true) {
                        if (l < j1) {
                            String[] astring5 = astring4[l];

                            if (!ArrayUtils.contains(astring5, s2)) {
                                ++l;
                                continue;
                            }

                            flag = true;
                        }

                        if (!flag) {
                            throw new JsonSyntaxException("Criterion '" + s2 + "' isn't a requirement for completion. This isn't supported behaviour, all criteria must be required.");
                        }
                        break;
                    }
                }

                return new Advancement.SerializedAdvancement(minecraftkey, advancementdisplay, advancementrewards, map, astring);
            }
        }

        public static Advancement.SerializedAdvancement b(PacketDataSerializer packetdataserializer) {
            MinecraftKey minecraftkey = packetdataserializer.readBoolean() ? packetdataserializer.l() : null;
            AdvancementDisplay advancementdisplay = packetdataserializer.readBoolean() ? AdvancementDisplay.b(packetdataserializer) : null;
            Map<String, Criterion> map = Criterion.c(packetdataserializer);
            String[][] astring = new String[packetdataserializer.g()][];

            for (int i = 0; i < astring.length; ++i) {
                astring[i] = new String[packetdataserializer.g()];

                for (int j = 0; j < astring[i].length; ++j) {
                    astring[i][j] = packetdataserializer.e(32767);
                }
            }

            return new Advancement.SerializedAdvancement(minecraftkey, advancementdisplay, AdvancementRewards.a, map, astring);
        }

        public Map<String, Criterion> c() {
            return this.e;
        }
    }
}
