package eu.trixcms.trixcore.common.i18n;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import eu.trixcms.trixcore.api.i18n.IMessageSource;
import eu.trixcms.trixcore.api.i18n.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JsonMessageSource implements IMessageSource {

    private static final Logger logger = LoggerFactory.getLogger(JsonMessageSource.class);

    private JsonObject source;

    public JsonMessageSource(Lang lang) {
        try {
            InputStream in = getClass().getResourceAsStream("/lang/" + lang.getLocale() + ".json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            source = (new Gson()).fromJson(reader, JsonObject.class);
        } catch (Exception e) {
            logger.error("Unable to load '" + lang.getLangName() + "' lang file");
        }
    }

    @Override
    public String get(String key) {
        if (source.has(key))
            return source.get(key).getAsString();

        return key;
    }
}
