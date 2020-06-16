package eu.trixcms.trixcore.common.mock;

import eu.trixcms.trixcore.api.i18n.IMessageSource;
import eu.trixcms.trixcore.api.i18n.Lang;

import java.util.HashMap;
import java.util.Map;

public class MessageSourceMock implements IMessageSource {

    private Map<String, String> source;

    public MessageSourceMock(Lang lang) {
        this();
    }

    public MessageSourceMock() {
        source = new HashMap<>();
    }

    public void add(String key, String value) {
        if (source.containsKey(key))
            source.replace(key, value);
        else
            source.put(key, value);
    }

    @Override
    public String get(String key) {
        if (source.containsKey(key))
            return source.get(key);

        return key;
    }
}
