package net.minecraft.server;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ArgumentProfile implements ArgumentType<ArgumentProfile.a> {

    private static final Collection<String> b = Arrays.asList("Player", "0123", "dd12be42-52a9-4a91-a8a1-11c01849e498", "@e");
    public static final SimpleCommandExceptionType a = new SimpleCommandExceptionType(new ChatMessage("argument.player.unknown", new Object[0]));

    public ArgumentProfile() {}

    public static Collection<GameProfile> a(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        return ((ArgumentProfile.a) commandcontext.getArgument(s, ArgumentProfile.a.class)).getNames((CommandListenerWrapper) commandcontext.getSource());
    }

    public static ArgumentProfile a() {
        return new ArgumentProfile();
    }

    public ArgumentProfile.a parse(StringReader stringreader) throws CommandSyntaxException {
        if (stringreader.canRead() && stringreader.peek() == '@') {
            ArgumentParserSelector argumentparserselector = new ArgumentParserSelector(stringreader);
            EntitySelector entityselector = argumentparserselector.s();

            if (entityselector.b()) {
                throw ArgumentEntity.c.create();
            } else {
                return new ArgumentProfile.b(entityselector);
            }
        } else {
            int i = stringreader.getCursor();

            while (stringreader.canRead() && stringreader.peek() != ' ') {
                stringreader.skip();
            }

            String s = stringreader.getString().substring(i, stringreader.getCursor());

            return (commandlistenerwrapper) -> {
                GameProfile gameprofile = commandlistenerwrapper.getServer().getModernUserCache().acquire(s); // Akarin

                if (gameprofile == null) {
                    throw ArgumentProfile.a.create();
                } else {
                    return Collections.singleton(gameprofile);
                }
            };
        }
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        if (commandcontext.getSource() instanceof ICompletionProvider) {
            StringReader stringreader = new StringReader(suggestionsbuilder.getInput());

            stringreader.setCursor(suggestionsbuilder.getStart());
            ArgumentParserSelector argumentparserselector = new ArgumentParserSelector(stringreader);

            try {
                argumentparserselector.s();
            } catch (CommandSyntaxException commandsyntaxexception) {
                ;
            }

            return argumentparserselector.a(suggestionsbuilder, (suggestionsbuilder1) -> {
                ICompletionProvider.b((Iterable) ((ICompletionProvider) commandcontext.getSource()).l(), suggestionsbuilder1);
            });
        } else {
            return Suggestions.empty();
        }
    }

    public Collection<String> getExamples() {
        return ArgumentProfile.b;
    }

    public static class b implements ArgumentProfile.a {

        private final EntitySelector a;

        public b(EntitySelector entityselector) {
            this.a = entityselector;
        }

        public Collection<GameProfile> getNames(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
            List<EntityPlayer> list = this.a.d(commandlistenerwrapper);

            if (list.isEmpty()) {
                throw ArgumentEntity.e.create();
            } else {
                List<GameProfile> list1 = Lists.newArrayList();
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                    list1.add(entityplayer.getProfile());
                }

                return list1;
            }
        }
    }

    @FunctionalInterface
    public interface a {

        Collection<GameProfile> getNames(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException;
    }
}
