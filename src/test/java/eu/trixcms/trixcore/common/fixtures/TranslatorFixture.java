package eu.trixcms.trixcore.common.fixtures;

import eu.trixcms.trixcore.api.i18n.Lang;
import eu.trixcms.trixcore.common.i18n.Translator;
import eu.trixcms.trixcore.common.mock.MessageSourceMock;

public class TranslatorFixture {

    public static Translator get() {
        return get(MessageSourceMock.class, Lang.values());
    }

    public static Translator get(Class<?> source) {
        return get(source, Lang.values());
    }

    public static Translator get(Lang main) {
        return get(MessageSourceMock.class, main, Lang.values());
    }

    public static Translator get(Class<?> source, Lang main, Lang... langs) {
        return new Translator(source, main, Lang.values());
    }

    public static Translator get(Class<?> source, Lang... langs) {
        return new Translator(source, langs);
    }
}
