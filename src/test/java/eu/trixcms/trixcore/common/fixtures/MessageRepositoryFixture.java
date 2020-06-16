package eu.trixcms.trixcore.common.fixtures;

import com.google.common.collect.Lists;
import eu.trixcms.trixcore.api.i18n.Lang;
import eu.trixcms.trixcore.common.i18n.MessageRepository;
import eu.trixcms.trixcore.common.mock.MessageSourceMock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageRepositoryFixture {

    public static MessageRepository create() {
        return create(Lang.FRENCH);
    }

    public static MessageRepository create(Lang lang) {
        return create(lang, null);
    }

    public static MessageRepository create(String... keysvalues) {
        return create(Lang.FRENCH, keysvalues);
    }

    public static MessageRepository create(Lang lang, String... keysvalues) {
        MessageSourceMock mock = new MessageSourceMock();

        if (keysvalues != null) {
            if (keysvalues.length % 2 != 0)
                throw new RuntimeException("Invalid create params number");

            if (keysvalues.length >= 2) {
                List<List<String>> parts = Lists.partition(new ArrayList<>(Arrays.asList(keysvalues)), 2);
                parts.forEach(part -> {
                    mock.add(part.get(0), part.get(1));
                });
            }
        }


        return new MessageRepository(lang, mock);
    }
}
