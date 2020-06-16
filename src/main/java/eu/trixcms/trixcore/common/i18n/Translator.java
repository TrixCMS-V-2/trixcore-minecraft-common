package eu.trixcms.trixcore.common.i18n;

import eu.trixcms.trixcore.api.i18n.IMessageSource;
import eu.trixcms.trixcore.api.i18n.ITranslator;
import eu.trixcms.trixcore.api.i18n.Lang;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Translator implements ITranslator<MessageRepository> {

    private static final Logger logger = LoggerFactory.getLogger(Translator.class);

    @Getter
    private Map<Lang, MessageRepository> cache;

    private Lang main;

    public Translator(Class<?> sourceClazz, Lang... supportedLang) {
        this(sourceClazz, Lang.getLangByValue(Locale.getDefault().getLanguage()), supportedLang);
    }

    public Translator(Class<?> sourceClazz, Lang main, Lang... supportedLang) {
        cache = new HashMap<>();

        for (Lang lang : supportedLang) {
            IMessageSource source = null;
            try {
                source = (IMessageSource) sourceClazz.getConstructor(Lang.class).newInstance(lang);
                cache.put(lang, new MessageRepository(lang, source));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                logger.error("Can't instantiate a new " + sourceClazz.getName() + " source repository", e);
            }
        }

        this.main = main;
    }

    public void changeMainLanguage(Lang lang) {
        main = lang;
    }

    @Override
    public String translate(Lang lang, String message_key, String... toReplace) {
        if (cache.containsKey(lang)) {
            String msg = cache.get(lang).translate(message_key);
            for (int i = 0; i < toReplace.length; i++)
                msg = msg.replace("{" + i + "}", toReplace[i]);

            return msg.replace("&", "§");
        } else
            return message_key;
    }

    @Override
    public String translate(Lang lang, String message_key, Boolean useCache, String... toReplace) {
        if (cache.containsKey(lang)) {
            String msg = cache.get(lang).translate(message_key, useCache);
            for (int i = 0; i < toReplace.length; i++)
                msg = msg.replace("{" + i + "}", toReplace[i]);

            return msg.replace("&", "§");
        } else
            return message_key;
    }

    @Override
    public String translate(String message_key, Boolean useCache, String... toReplace) {
        return translate(main, message_key, useCache, toReplace);
    }

    @Override
    public String translate(String message_key, String... toReplace) {
        return translate(main, message_key, toReplace);
    }

    public String of(String message_key, String... toReplace) {
        return translate(message_key, toReplace);
    }

    public String of(String message_key, Boolean useCache, String... toReplace) {
        return translate(message_key, useCache, toReplace);
    }

    @Override
    public String translateFull(String message) {
        return translateFull(main, message);
    }

    @Override
    public String translateFull(Lang lang, String message) {
        String regex = "\\{\\{[a-zA-Z0-9-._]+\\}\\}";
        Pattern pattern = Pattern.compile(regex);
        for (String m : message.split(" "))
            if (m.startsWith("{{") && m.endsWith("}}")) {
                Matcher matcher = pattern.matcher(m);
                String message_key = m.replace(matcher.replaceAll(""), "").replace("{", "").replace("}", "");
                if (!message_key.equals(""))
                    message = message.replace("{{" + message_key + "}}", translate(lang, message_key, ""));
            }

        return message;
    }

    @Override
    public Lang getActiveLanguage() {
        return main;
    }

    public void clear() {
        for (Map.Entry<Lang, MessageRepository> message : cache.entrySet())
            message.getValue().clear();
    }

    @Override
    public Map<Lang, MessageRepository> get() {
        return cache;
    }
}
