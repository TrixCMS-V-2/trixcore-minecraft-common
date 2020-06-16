package eu.trixcms.trixcore.common.i18n;

import eu.trixcms.trixcore.api.i18n.IMessageRepository;
import eu.trixcms.trixcore.api.i18n.IMessageSource;
import eu.trixcms.trixcore.api.i18n.Lang;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MessageRepository implements IMessageRepository {

    @Getter
    private Lang lang;

    private Map<String, String> cache;

    private IMessageSource source;

    public MessageRepository(Lang lang, IMessageSource source) {
        this.lang = lang;
        cache = new HashMap<>();
        this.source = source;
    }

    @Override
    public String translate(String message_key) {
        if (cache.containsKey(message_key))
            return cache.get(message_key);
        else {
            cache.put(message_key, source.get(message_key));
            return cache.get(message_key);
        }
    }

    @Override
    public String translate(String message_key, Boolean useCache) {
        if (useCache)
            return translate(message_key);
        else
            return source.get(message_key);
    }

    @Override
    public IMessageSource getSource() {
        return source;
    }

    @Override
    public Map<String, String> get() {
        return cache;
    }

    public void clear() {
        cache.clear();
    }
}
