package eu.trixcms.trixcore.common.server;

import eu.trixcms.trixcore.api.config.IConfig;
import eu.trixcms.trixcore.api.server.exception.InvalidPortException;
import eu.trixcms.trixcore.common.TrixServer;
import eu.trixcms.trixcore.common.mock.ConfigMock;
import org.junit.Test;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;

public class ConfigTest {

    @Test
    public void init() throws InvalidPortException, IOException {
        TrixServer server = new TrixServer();
        IConfig config = new ConfigMock("a secret key", 8888);
        server.config(config);

        assertThat(server.config().getSecretKey()).isEqualTo(config.getSecretKey());
        assertThat(server.config().getServerPort()).isEqualTo(config.getServerPort());
    }

    @Test
    public void validPort() throws InvalidPortException, IOException {
        TrixServer server = new TrixServer();
        server.config(new ConfigMock("", 8888));
    }

    @Test(expected = InvalidPortException.class)
    public void invalidPort() throws InvalidPortException, IOException {
        TrixServer server = new TrixServer();
        server.config(new ConfigMock("", 0));
    }
}
