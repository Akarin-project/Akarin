package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LocaleLanguage {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern b = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    private static final LocaleLanguage c = new LocaleLanguage();
    private final Map<String, String> d = Maps.newHashMap();
    private long e;

    public LocaleLanguage() {
        try {
            InputStream inputstream = LocaleLanguage.class.getResourceAsStream("/assets/minecraft/lang/en_us.json");
            Throwable throwable = null;

            try {
                JsonElement jsonelement = (JsonElement) (new Gson()).fromJson(new InputStreamReader(inputstream, StandardCharsets.UTF_8), JsonElement.class);
                JsonObject jsonobject = ChatDeserializer.m(jsonelement, "strings");
                Iterator iterator = jsonobject.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry<String, JsonElement> entry = (Entry) iterator.next();
                    String s = LocaleLanguage.b.matcher(ChatDeserializer.a((JsonElement) entry.getValue(), (String) entry.getKey())).replaceAll("%$1s");

                    this.d.put(entry.getKey(), s);
                }

                this.e = SystemUtils.getMonotonicMillis();
            } catch (Throwable throwable1) {
                throwable = throwable1;
                throw throwable1;
            } finally {
                if (inputstream != null) {
                    if (throwable != null) {
                        try {
                            inputstream.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    } else {
                        inputstream.close();
                    }
                }

            }
        } catch (JsonParseException | IOException ioexception) {
            LocaleLanguage.LOGGER.error("Couldn't read strings from /assets/minecraft/lang/en_us.json", ioexception);
        }

    }

    public static LocaleLanguage getInstance() { return a(); } // Paper - OBFHELPER
    public static LocaleLanguage a() {
        return LocaleLanguage.c;
    }

    public synchronized String translateKey(String key) { return a(key); } // Paper - OBFHELPER
    public synchronized String a(String s) {
        return this.c(s);
    }

    private String c(String s) {
        String s1 = (String) this.d.get(s);

        return s1 == null ? s : s1;
    }

    public synchronized boolean b(String s) {
        return this.d.containsKey(s);
    }

    public long b() {
        return this.e;
    }
}
