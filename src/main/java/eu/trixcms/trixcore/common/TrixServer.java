package eu.trixcms.trixcore.common;

import eu.trixcms.trixcore.api.command.ICommandManager;
import eu.trixcms.trixcore.api.config.IConfig;
import eu.trixcms.trixcore.api.config.exception.InvalidConfigException;
import eu.trixcms.trixcore.api.container.CommandContainer;
import eu.trixcms.trixcore.api.i18n.ITranslator;
import eu.trixcms.trixcore.api.method.IMethod;
import eu.trixcms.trixcore.api.method.IMethodsManager;
import eu.trixcms.trixcore.api.method.exception.DuplicateMethodNameException;
import eu.trixcms.trixcore.api.method.exception.InvalidMethodDefinitionException;
import eu.trixcms.trixcore.api.scheduler.IScheduler;
import eu.trixcms.trixcore.api.server.IServer;
import eu.trixcms.trixcore.api.server.exception.InvalidPortException;
import eu.trixcms.trixcore.api.util.ServerTypeEnum;
import eu.trixcms.trixcore.common.i18n.MessageRepository;
import eu.trixcms.trixcore.common.request.RequestHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

@Accessors(fluent = true)
public class TrixServer implements IServer<CommandContainer, MessageRepository> {

    private static final Logger logger = LoggerFactory.getLogger(TrixServer.class);

    @Getter private IConfig config;
    @Getter @Setter private IScheduler scheduler;
    @Getter @Setter private ITranslator<MessageRepository> translator;
    @Setter @Getter private ServerTypeEnum serverType;
    @Setter @Getter private ICommandManager<CommandContainer> commandManager;

    private IMethodsManager method = new MethodManager();
    private boolean running = false;
    private Server server;

    @Override
    public void start() throws InvalidPortException, InvalidConfigException {
        if (config == null)
            throw new InvalidConfigException();

        if (config.getServerPort() == null || !isPortValid(config.getServerPort()))
            throw new InvalidPortException(null);

        if (translator == null || scheduler == null || commandManager == null) {
            throw new RuntimeException("TrixCore not properly initialized");
        }

        Instant start = Instant.now();

        server = new Server(config.getServerPort());
        try {
            server.setHandler(new RequestHandler(method, config, translator, serverType));
            server.start();

            Duration timeExecution = Duration.between(start, Instant.now());
            logger.info(translator.of("HTTP_SERVER_LISTENING", InetAddress.getLocalHost().getHostAddress(), config.getServerPort() + "", timeExecution.toMillis() + ""));
            running = true;
        } catch (Exception e) {
            logger.error("Failed to start the server", e);
            running = false;
        }
    }

    @Override
    public void stop() {
        if (running) {
            try {
                server.stop();
                logger.info(translator.of("HTTP_SERVER_SUCCESSFULLY_STOPPED"));
            } catch (Exception e) {
                logger.error("Failed to stop the server", e);
            }
            running = false;
        }
    }

    @Override
    public TrixServer setPort(Integer port) throws InvalidPortException, IOException {
        if (port == -1) {
            config.saveServerPort(0);
        } else if (isPortValid(port)) {
            if (!config.getServerPort().equals(port)) {
                config.saveServerPort(port);
            }
        }

        return this;
    }

    @Override
    public TrixServer setSecretKey(String key) throws IOException {
        if (!config.getSecretKey().equals(key)) {
            config.saveSecretKey(key);
        }

        return this;
    }

    @Override
    public TrixServer registerMethod(IMethod IMethod) throws DuplicateMethodNameException, InvalidMethodDefinitionException {
        this.method.addMethod(IMethod);
        return this;
    }

    @Override
    public TrixServer registerMethods(IMethod... IMethod) throws DuplicateMethodNameException, InvalidMethodDefinitionException {
        this.method.addMethods(Arrays.asList(IMethod));
        return this;
    }

    private boolean isPortValid(Integer port) throws InvalidPortException {
        if (port == 0 || port < 1000)
            throw new InvalidPortException(port);
        return true;
    }

    @Override
    public TrixServer config(IConfig config) throws InvalidPortException, IOException {
        this.config = config;
        setPort(config.getServerPort());
        setSecretKey(config.getSecretKey());

        return this;
    }
}
