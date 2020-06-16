package eu.trixcms.trixcore.common.i18n;

import eu.trixcms.trixcore.api.i18n.Lang;
import eu.trixcms.trixcore.common.fixtures.MessageRepositoryFixture;
import eu.trixcms.trixcore.common.fixtures.MessageSourceFixture;
import eu.trixcms.trixcore.common.mock.MessageSourceMock;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class MessageTest {

    @Test
    public void translateMessageNoCache() {
        MessageRepository repository = MessageRepositoryFixture.create("a_key", "a value");
        MessageSourceMock source = MessageSourceFixture.cast(repository);

        String retrieved = repository.translate("a_key", false);
        assertThat(retrieved).isEqualTo("a value");

        source.add("a_key", "new value");

        String retrieved2 = repository.translate("a_key", false);
        assertThat(retrieved2).isEqualTo("new value");
    }

    @Test
    public void translateMessageWithCache() {
        MessageRepository repository = MessageRepositoryFixture.create("a_key", "a value");
        MessageSourceMock source = MessageSourceFixture.cast(repository);

        String retrieved = repository.translate("a_key");
        assertThat(retrieved).isEqualTo("a value");

        source.add("a_key", "new value");

        String retrieved2 = repository.translate("a_key");
        assertThat(retrieved2).isEqualTo("a value");
    }

    @Test
    public void translateNotFoundMessageKey() {
        MessageRepository repository = MessageRepositoryFixture.create();

        String retrieved = repository.translate("a_bad_key");

        assertThat(retrieved).isEqualTo("a_bad_key");
    }

    @Test
    public void clearCache(){
        MessageRepository repository = MessageRepositoryFixture.create("a_key", "a value");
        MessageSourceMock source = MessageSourceFixture.cast(repository);

        assertThat(repository.translate("a_key")).isEqualTo("a value");
        source.add("a_key", "new value");
        assertThat(repository.translate("a_key")).isEqualTo("a value");
        repository.clear();
        assertThat(repository.translate("a_key")).isEqualTo("new value");
    }

}
