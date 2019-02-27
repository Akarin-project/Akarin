package net.minecraft.server;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandDebug {

    private static final Logger a = LogManager.getLogger();
    private static final SimpleCommandExceptionType b = new SimpleCommandExceptionType(new ChatMessage("commands.debug.notRunning", new Object[0]));
    private static final SimpleCommandExceptionType c = new SimpleCommandExceptionType(new ChatMessage("commands.debug.alreadyRunning", new Object[0]));

    public static void a(com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> com_mojang_brigadier_commanddispatcher) {
        com_mojang_brigadier_commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandDispatcher.a("debug").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(3);
        })).then(CommandDispatcher.a("start").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource());
        }))).then(CommandDispatcher.a("stop").executes((commandcontext) -> {
            return b((CommandListenerWrapper) commandcontext.getSource());
        })));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
        // CraftBukkit start - only allow use when enabled (so that no blank profile results occur)
        if (!commandlistenerwrapper.getServer().methodProfiler.ENABLED) {
            commandlistenerwrapper.sendFailureMessage(new ChatComponentText("Vanilla debug profiling is disabled."));
            commandlistenerwrapper.sendFailureMessage(new ChatComponentText("To enable, restart the server with `-DenableDebugMethodProfiler=true' before `-jar'."));
            commandlistenerwrapper.sendFailureMessage(new ChatComponentText("Use `/timings' for plugin timings."));
            return 0;
        }
        // CraftBukkit end
        MinecraftServer minecraftserver = commandlistenerwrapper.getServer();
        MethodProfiler methodprofiler = minecraftserver.methodProfiler;

        if (methodprofiler.a()) {
            throw CommandDebug.c.create();
        } else {
            minecraftserver.ai();
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.debug.started", new Object[] { "Started the debug profiler. Type '/debug stop' to stop it."}), true);
            return 0;
        }
    }

    private static int b(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
        // CraftBukkit start - only allow use when enabled (so that no blank profile results occur)
        if (!commandlistenerwrapper.getServer().methodProfiler.ENABLED) {
            commandlistenerwrapper.sendFailureMessage(new ChatComponentText("Vanilla debug profiling is disabled."));
            commandlistenerwrapper.sendFailureMessage(new ChatComponentText("To enable, restart the server with `-DenableDebugMethodProfiler=true' before `-jar'."));
            commandlistenerwrapper.sendFailureMessage(new ChatComponentText("Use `/timings' for plugin timings."));
            return 0;
        }
        // CraftBukkit end
        MinecraftServer minecraftserver = commandlistenerwrapper.getServer();
        MethodProfiler methodprofiler = minecraftserver.methodProfiler;

        if (!methodprofiler.a()) {
            throw CommandDebug.b.create();
        } else {
            long i = SystemUtils.getMonotonicNanos();
            int j = minecraftserver.ah();
            long k = i - methodprofiler.c();
            int l = j - methodprofiler.d();
            File file = new File(minecraftserver.c("debug"), "profile-results-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + ".txt");

            file.getParentFile().mkdirs();
            OutputStreamWriter outputstreamwriter = null;

            try {
                outputstreamwriter = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
                outputstreamwriter.write(a(k, l, methodprofiler));
            } catch (Throwable throwable) {
                CommandDebug.a.error("Could not save profiler results to {}", file, throwable);
            } finally {
                IOUtils.closeQuietly(outputstreamwriter);
            }

            methodprofiler.b();
            float f = (float) k / 1.0E9F;
            float f1 = (float) l / f;

            commandlistenerwrapper.sendMessage(new ChatMessage("commands.debug.stopped", new Object[] { String.format(Locale.ROOT, "%.2f", f), l, String.format("%.2f", f1)}), true);
            return MathHelper.d(f1);
        }
    }

    private static String a(long i, int j, MethodProfiler methodprofiler) {
        StringBuilder stringbuilder = new StringBuilder();

        stringbuilder.append("---- Minecraft Profiler Results ----\n");
        stringbuilder.append("// ");
        stringbuilder.append(a());
        stringbuilder.append("\n\n");
        stringbuilder.append("Time span: ").append(i).append(" ms\n");
        stringbuilder.append("Tick span: ").append(j).append(" ticks\n");
        stringbuilder.append("// This is approximately ").append(String.format(Locale.ROOT, "%.2f", (float) j / ((float) i / 1.0E9F))).append(" ticks per second. It should be ").append(20).append(" ticks per second\n\n");
        stringbuilder.append("--- BEGIN PROFILE DUMP ---\n\n");
        a(0, "root", stringbuilder, methodprofiler);
        stringbuilder.append("--- END PROFILE DUMP ---\n\n");
        return stringbuilder.toString();
    }

    private static void a(int i, String s, StringBuilder stringbuilder, MethodProfiler methodprofiler) {
        List<MethodProfiler.ProfilerInfo> list = methodprofiler.b(s);

        if (list != null && list.size() >= 3) {
            for (int j = 1; j < list.size(); ++j) {
                MethodProfiler.ProfilerInfo methodprofiler_profilerinfo = (MethodProfiler.ProfilerInfo) list.get(j);

                stringbuilder.append(String.format("[%02d] ", i));

                for (int k = 0; k < i; ++k) {
                    stringbuilder.append("|   ");
                }

                stringbuilder.append(methodprofiler_profilerinfo.c).append(" - ").append(String.format(Locale.ROOT, "%.2f", methodprofiler_profilerinfo.a)).append("%/").append(String.format(Locale.ROOT, "%.2f", methodprofiler_profilerinfo.b)).append("%\n");
                if (!"unspecified".equals(methodprofiler_profilerinfo.c)) {
                    try {
                        a(i + 1, s + "." + methodprofiler_profilerinfo.c, stringbuilder, methodprofiler);
                    } catch (Exception exception) {
                        stringbuilder.append("[[ EXCEPTION ").append(exception).append(" ]]");
                    }
                }
            }

        }
    }

    private static String a() {
        String[] astring = new String[] { "Shiny numbers!", "Am I not running fast enough? :(", "I'm working as hard as I can!", "Will I ever be good enough for you? :(", "Speedy. Zoooooom!", "Hello world", "40% better than a crash report.", "Now with extra numbers", "Now with less numbers", "Now with the same numbers", "You should add flames to things, it makes them go faster!", "Do you feel the need for... optimization?", "*cracks redstone whip*", "Maybe if you treated it better then it'll have more motivation to work faster! Poor server."};

        try {
            return astring[(int) (SystemUtils.getMonotonicNanos() % (long) astring.length)];
        } catch (Throwable throwable) {
            return "Witty comment unavailable :(";
        }
    }
}
