package net.minecraft.server;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayDeque;
import java.util.List;
import javax.annotation.Nullable;

public class CustomFunction {

    private final CustomFunction.c[] a;
    private final MinecraftKey b;

    public CustomFunction(MinecraftKey minecraftkey, CustomFunction.c[] acustomfunction_c) {
        this.b = minecraftkey;
        this.a = acustomfunction_c;
    }

    public MinecraftKey a() {
        return this.b;
    }

    public CustomFunction.c[] b() {
        return this.a;
    }

    public static CustomFunction a(MinecraftKey minecraftkey, CustomFunctionData customfunctiondata, List<String> list) {
        List<CustomFunction.c> list1 = Lists.newArrayListWithCapacity(list.size());

        for (int i = 0; i < list.size(); ++i) {
            int j = i + 1;
            String s = ((String) list.get(i)).trim();
            StringReader stringreader = new StringReader(s);

            if (stringreader.canRead() && stringreader.peek() != '#') {
                if (stringreader.peek() == '/') {
                    stringreader.skip();
                    if (stringreader.peek() == '/') {
                        throw new IllegalArgumentException("Unknown or invalid command '" + s + "' on line " + j + " (if you intended to make a comment, use '#' not '//')");
                    }

                    String s1 = stringreader.readUnquotedString();

                    throw new IllegalArgumentException("Unknown or invalid command '" + s + "' on line " + j + " (did you mean '" + s1 + "'? Do not use a preceding forwards slash.)");
                }

                try {
                    ParseResults<CommandListenerWrapper> parseresults = customfunctiondata.d().parse(stringreader, customfunctiondata.f()); // CraftBukkit

                    if (parseresults.getReader().canRead()) {
                        if (parseresults.getExceptions().size() == 1) {
                            throw (CommandSyntaxException) parseresults.getExceptions().values().iterator().next();
                        }

                        if (parseresults.getContext().getRange().isEmpty()) {
                            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(parseresults.getReader());
                        }

                        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(parseresults.getReader());
                    }

                    list1.add(new CustomFunction.b(parseresults));
                } catch (CommandSyntaxException commandsyntaxexception) {
                    throw new IllegalArgumentException("Whilst parsing command on line " + j + ": " + commandsyntaxexception.getMessage());
                }
            }
        }

        return new CustomFunction(minecraftkey, (CustomFunction.c[]) list1.toArray(new CustomFunction.c[0]));
    }

    public static class a {

        public static final CustomFunction.a a = new CustomFunction.a((MinecraftKey) null);
        @Nullable
        private final MinecraftKey b;
        private boolean c;
        private CustomFunction d;

        public a(@Nullable MinecraftKey minecraftkey) {
            this.b = minecraftkey;
        }

        public a(CustomFunction customfunction) {
            this.b = null;
            this.d = customfunction;
        }

        @Nullable
        public CustomFunction a(CustomFunctionData customfunctiondata) {
            if (!this.c) {
                if (this.b != null) {
                    this.d = customfunctiondata.a(this.b);
                }

                this.c = true;
            }

            return this.d;
        }

        @Nullable
        public MinecraftKey a() {
            return this.d != null ? this.d.b : this.b;
        }
    }

    public static class d implements CustomFunction.c {

        private final CustomFunction.a a;

        public d(CustomFunction customfunction) {
            this.a = new CustomFunction.a(customfunction);
        }

        public void a(CustomFunctionData customfunctiondata, CommandListenerWrapper commandlistenerwrapper, ArrayDeque<CustomFunctionData.a> arraydeque, int i) {
            CustomFunction customfunction = this.a.a(customfunctiondata);

            if (customfunction != null) {
                CustomFunction.c[] acustomfunction_c = customfunction.b();
                int j = i - arraydeque.size();
                int k = Math.min(acustomfunction_c.length, j);

                for (int l = k - 1; l >= 0; --l) {
                    arraydeque.addFirst(new CustomFunctionData.a(customfunctiondata, commandlistenerwrapper, acustomfunction_c[l]));
                }
            }

        }

        public String toString() {
            return "function " + this.a.a();
        }
    }

    public static class b implements CustomFunction.c {

        private final ParseResults<CommandListenerWrapper> a;

        public b(ParseResults<CommandListenerWrapper> parseresults) {
            this.a = parseresults;
        }

        public void a(CustomFunctionData customfunctiondata, CommandListenerWrapper commandlistenerwrapper, ArrayDeque<CustomFunctionData.a> arraydeque, int i) throws CommandSyntaxException {
            customfunctiondata.d().execute(new ParseResults(this.a.getContext().withSource(commandlistenerwrapper), this.a.getStartIndex(), this.a.getReader(), this.a.getExceptions()));
        }

        public String toString() {
            return this.a.getReader().getString();
        }
    }

    public interface c {

        void a(CustomFunctionData customfunctiondata, CommandListenerWrapper commandlistenerwrapper, ArrayDeque<CustomFunctionData.a> arraydeque, int i) throws CommandSyntaxException;
    }
}
