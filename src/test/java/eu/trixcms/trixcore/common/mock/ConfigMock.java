package eu.trixcms.trixcore.common.mock;

import eu.trixcms.trixcore.api.config.IConfig;

import java.io.IOException;

public class ConfigMock implements IConfig {

    private String secretKey;
    private Integer port;

    public ConfigMock(String secretKey, Integer port) {
        this.secretKey = secretKey;
        this.port = port;
    }

    @Override
    public void saveSecretKey(String key) throws IOException {
        this.secretKey = key;
    }

    @Override
    public String getSecretKey() {
        return this.secretKey;
    }

    @Override
    public void saveServerPort(Integer port) throws IOException {
        this.port = port;
    }

    @Override
    public Integer getServerPort() {
        return this.port;
    }
}
