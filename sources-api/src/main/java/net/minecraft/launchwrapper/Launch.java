package net.minecraft.launchwrapper;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.MixinEnvironment.Side;

import io.akarin.launcher.AkarinMixinConfig;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Launch {
    private static final Logger logger = LogManager.getLogger("LaunchWrapper");
    private static final String DEFAULT_TWEAK = "org.spongepowered.asm.launch.MixinTweaker";
    public static LaunchClassLoader classLoader;
    public static Map<String,Object> blackboard = new HashMap<>();

    public static void main(String[] args) {
        new Launch().launch(args);
    }

    private Launch() {
        if (getClass().getClassLoader() instanceof URLClassLoader) {
            final URLClassLoader ucl = (URLClassLoader) getClass().getClassLoader();
            classLoader = new LaunchClassLoader(ucl.getURLs());
        } else {
            classLoader = new LaunchClassLoader(getURLs());
        }
        Thread.currentThread().setContextClassLoader(classLoader);
    }
    
    private void configureMixin() {
        MixinEnvironment.getDefaultEnvironment().setSide(Side.SERVER);
        Mixins.addConfiguration("mixins.akarin.core.json");
        
        AkarinMixinConfig.init(new File("akarin.yml"));
    }

    private URL[] getURLs() {
        String cp = System.getProperty("java.class.path");
        String[] elements = cp.split(File.pathSeparator);
        if (elements.length == 0) {
            elements = new String[] { "" };
        }
        URL[] urls = new URL[elements.length];
        for (int i = 0; i < elements.length; i++) {
            try {
                URL url = new File(elements[i]).toURI().toURL();
                urls[i] = url;
            } catch (MalformedURLException ignore) {
                // malformed file string or class path element does not exist
            }
        }
        return urls;
    }

    private void launch(String[] args) {
        final OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();

        final OptionSpec<String> tweakClassOption = parser.accepts("tweakClass", "Tweak class(es) to load").withRequiredArg().defaultsTo(DEFAULT_TWEAK);
        final OptionSpec<String> nonOption = parser.nonOptions();

        final OptionSet options = parser.parse(args);
        final List<String> tweakClassNames = new ArrayList<>(options.valuesOf(tweakClassOption));

        final List<String> argumentList = new ArrayList<>();
        // This list of names will be interacted with through tweakers. They can append to this list
        // any 'discovered' tweakers from their preferred mod loading mechanism
        // By making this object discoverable and accessible it's possible to perform
        // things like cascading of tweakers
        blackboard.put("TweakClasses", tweakClassNames);

        // This argument list will be constructed from all tweakers. It is visible here so
        // all tweakers can figure out if a particular argument is present, and add it if not
        blackboard.put("ArgumentList", argumentList);

        // This is to prevent duplicates - in case a tweaker decides to add itself or something
        final Set<String> visitedTweakerNames = new HashSet<>();
        // The 'definitive' list of tweakers
        final List<ITweaker> allTweakers = new ArrayList<>();
        try {
            final List<ITweaker> pendingTweakers = new ArrayList<>(tweakClassNames.size() + 1);
            // The list of tweak instances - may be useful for interoperability
            blackboard.put("Tweaks", pendingTweakers);
            // The primary tweaker (the first one specified on the command line) will actually
            // be responsible for providing the 'main' name and generally gets called first
            ITweaker primaryTweaker = null;
            // This loop will terminate, unless there is some sort of pathological tweaker
            // that reinserts itself with a new identity every pass
            // It is here to allow tweakers to "push" new tweak classes onto the 'stack' of
            // tweakers to evaluate allowing for cascaded discovery and injection of tweakers
            while (!tweakClassNames.isEmpty()) {
                for (final Iterator<String> it = tweakClassNames.iterator(); it.hasNext(); ) {
                    final String tweakName = it.next();
                    // Safety check - don't reprocess something we've already visited
                    if (visitedTweakerNames.contains(tweakName)) {
                        logger.log(Level.WARN, "Tweak class name {} has already been visited -- skipping", tweakName);
                        // remove the tweaker from the stack otherwise it will create an infinite loop
                        it.remove();
                        continue;
                    } else {
                        visitedTweakerNames.add(tweakName);
                    }
                    logger.info("Loading tweak class name {}", tweakName);

                    // Ensure we allow the tweak class to load with the parent classloader
                    classLoader.getClassLoaderExclusions().add(tweakName.substring(0, tweakName.lastIndexOf('.')));
                    final ITweaker tweaker = (ITweaker) Class.forName(tweakName, true, classLoader)
                            .getConstructor().newInstance();
                    pendingTweakers.add(tweaker);

                    // Remove the tweaker from the list of tweaker names we've processed this pass
                    it.remove();
                    // If we haven't visited a tweaker yet, the first will become the 'primary' tweaker
                    if (primaryTweaker == null) {
                        logger.info("Using primary tweak class name {}", tweakName);
                        primaryTweaker = tweaker;
                    }
                }

                // Configure environment to avoid warn
                configureMixin();
                
                // Now, iterate all the tweakers we just instantiated
                while(!pendingTweakers.isEmpty()) {
                    final ITweaker tweaker = pendingTweakers.remove(0);
                    logger.info("Calling tweak class {}", tweaker.getClass().getName());
                    tweaker.acceptOptions(options.valuesOf(nonOption));
                    tweaker.injectIntoClassLoader(classLoader);
                    allTweakers.add(tweaker);
                }
                // continue around the loop until there's no tweak classes
            }

            // Once we're done, we then ask all the tweakers for their arguments and add them all to the
            // master argument list
            for (final ITweaker tweaker : allTweakers) {
                argumentList.addAll(Arrays.asList(tweaker.getLaunchArguments()));
            }

            final String launchTarget = "org.bukkit.craftbukkit.Main";
            final Class<?> clazz = Class.forName(launchTarget, false, classLoader);
            final Method mainMethod = clazz.getMethod("main", String[].class);

            logger.info("Launching wrapped Minecraft {{}}", launchTarget);
            // Pass original server arguments
            argumentList.addAll(Arrays.asList(args));
            mainMethod.invoke(null, (Object) argumentList.toArray(new String[argumentList.size()]));
        } catch (Exception e) {
            logger.error("Unable to launch", e);
            System.exit(1);
        }
    }
}
