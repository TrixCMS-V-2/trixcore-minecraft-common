package eu.trixcms.trixcore.common.i18n;

import eu.trixcms.trixcore.api.i18n.Lang;
import eu.trixcms.trixcore.common.fixtures.MessageSourceFixture;
import eu.trixcms.trixcore.common.fixtures.TranslatorFixture;
import eu.trixcms.trixcore.common.mock.MessageSourceMock;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class TranslatorTest {

    @Test
    public void changeActiveLanguage() {
        Translator translator = TranslatorFixture.get(Lang.ENGLISH);
        translator.changeMainLanguage(Lang.FRENCH);

        Lang activeLanguage = translator.getActiveLanguage();

        assertThat(activeLanguage).isEqualTo(Lang.FRENCH);
    }

    @Test
    public void translateSimpleMessageWithCache() {
        Translator translator = TranslatorFixture.get(Lang.ENGLISH);
        MessageRepository repository = translator.get().get(Lang.ENGLISH);
        MessageSourceMock source = MessageSourceFixture.cast(repository);

        source.add("a_key", "a value");

        String retrieved = repository.translate("a_key");
        assertThat(retrieved).isEqualTo("a value");

        source.add("a_key", "new value");

        String retrieved2 = repository.translate("a_key");
        assertThat(retrieved2).isEqualTo("a value");
    }

    @Test
    public void translateSimpleMessageWithoutCache() {
        Translator translator = TranslatorFixture.get(Lang.ENGLISH);
        MessageRepository repository = translator.get().get(Lang.ENGLISH);
        MessageSourceMock source = MessageSourceFixture.cast(repository);

        source.add("a_key", "a value");

        String retrieved = repository.translate("a_key", false);
        assertThat(retrieved).isEqualTo("a value");

        source.add("a_key", "new value");

        String retrieved2 = repository.translate("a_key", false);
        assertThat(retrieved2).isEqualTo("new value");
    }

    @Test
    public void translateComplexMessage() {
        Translator translator = TranslatorFixture.get(Lang.ENGLISH);
        MessageRepository repository = translator.get().get(Lang.ENGLISH);
        MessageSourceMock source = MessageSourceFixture.cast(repository);

        source.add("a_key", "Hello {0} it's {1}");
        source.add("a_key_2", "Hello {1} it's {0}");

        String retrieved1 = translator.translate("a_key", "James", "Paul");
        String retrieved2 = translator.translate("a_key_2", "James", "Paul");

        assertThat(retrieved1).isEqualTo("Hello James it's Paul");
        assertThat(retrieved2).isEqualTo("Hello Paul it's James");
    }

    @Test
    public void translateMessageInDifferentLanguages(){
        Translator translator = TranslatorFixture.get(Lang.ENGLISH);
        MessageRepository repositoryEn = translator.get().get(Lang.ENGLISH);
        MessageRepository repositoryFr = translator.get().get(Lang.FRENCH);
        MessageSourceMock sourceEn = MessageSourceFixture.cast(repositoryEn);
        MessageSourceMock sourceFr = MessageSourceFixture.cast(repositoryFr);

        sourceFr.add("a_key", "Salut");
        sourceEn.add("a_key", "Hello");

        String retrieved1 = translator.translate(Lang.ENGLISH, "a_key");
        String retrieved2 = translator.translate(Lang.FRENCH, "a_key");

        assertThat(retrieved1).isEqualTo("Hello");
        assertThat(retrieved2).isEqualTo("Salut");
    }

    @Test
    public void autoTranslateMessage(){
        Translator translator = TranslatorFixture.get(Lang.ENGLISH);
        MessageRepository repository = translator.get().get(Lang.ENGLISH);
        MessageSourceMock source = MessageSourceFixture.cast(repository);

        source.add("a_key", "Hello");
        source.add("a_key_2", "World");

        String message = translator.translateFull("This is a {{a_key}} {{a_key_2}} message !");

        assertThat(message).isEqualTo("This is a Hello World message !");
    }
}
