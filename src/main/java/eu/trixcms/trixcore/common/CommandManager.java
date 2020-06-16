package eu.trixcms.trixcore.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import eu.trixcms.trixcore.api.command.ICommandExecutor;
import eu.trixcms.trixcore.api.command.ICommandManager;
import eu.trixcms.trixcore.api.container.CommandContainer;
import eu.trixcms.trixcore.common.i18n.Translator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class CommandManager implements ICommandManager<CommandContainer> {

    private static final Logger logger = LoggerFactory.getLogger(CommandManager.class);

    private File commandsFile;

    private List<CommandContainer> commands;

    private final Type COMMAND_TYPE = new TypeToken<List<CommandContainer>>() {}.getType();

    private Translator translator;

    private SchedulerManager scheduler;

    private ICommandExecutor<CommandContainer> executor;

    public CommandManager(ICommandExecutor<CommandContainer> executor, Translator translator, SchedulerManager scheduler, File commandsFile) {
        this.commands = new ArrayList<>();
        this.commandsFile = commandsFile;
        this.scheduler = scheduler;
        this.translator = translator;
        this.executor = executor;

        init();
    }

    private void init() {
        if (commandsFile.exists()) {
            commandsFile.setWritable(true);
            commandsFile.setReadable(true);
            Gson gson = new Gson();
            JsonReader reader;
            try {
                reader = new JsonReader(new FileReader(this.commandsFile));
                commands = gson.fromJson(reader, COMMAND_TYPE);
                logger.info(translator.of("TASKS_SUCCESSFULLY_LOADED", commands.size() + ""));
                for (CommandContainer cmd : this.commands) {
                    scheduler.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            executor.executeCommand(cmd);
                        }
                    }, cmd.getTime() * 1000);
                }
            } catch (FileNotFoundException e) {
                logger.error(translator.of("ERROR"), e);
            }
        } else {
            try (Writer writer = new FileWriter(this.commandsFile)) {
                Gson gson = new GsonBuilder().create();

                gson.toJson(this.commands, writer);
                commandsFile.setWritable(true);
                commandsFile.setReadable(true);
            } catch (IOException e) {
                logger.error(translator.of("ERROR"), e);
            }
        }
    }

    public void add(CommandContainer cmd) throws IOException {
        this.commands.add(cmd);
        logger.info(translator.of("TASKS_SAVING"));
        try (Writer writer = new FileWriter(this.commandsFile)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(this.commands, writer);
        }
    }

    @Override
    public List<CommandContainer> get() {
        return this.commands;
    }

    @Override
    public void clear() {
        this.commands.clear();
        try (Writer writer = new FileWriter(this.commandsFile)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(this.commands, writer);
        } catch (IOException e) {
            logger.error(translator.of("ERROR"), e);
        }
    }
}
