package eu.trixcms.trixcore.common;

import eu.trixcms.trixcore.api.scheduler.IScheduler;
import eu.trixcms.trixcore.common.i18n.Translator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

@RequiredArgsConstructor
public class SchedulerManager implements IScheduler {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerManager.class);

    private Timer timer = new Timer();

    private final Translator translator;

    @Override
    public void schedule(TimerTask task, int delay) {
        logger.info(translator.of("TASK_SCHEDULED_SUCCESSFULLY"));
        timer.schedule(task, 0, delay);
    }

    @Override
    public void resetScheduler() {
        logger.info(translator.of("TASKS_CANCELLED_SUCCESSFULLY"));
        timer.cancel();
        timer = new Timer();
    }

}
