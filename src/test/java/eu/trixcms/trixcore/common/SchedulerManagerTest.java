package eu.trixcms.trixcore.common;

import eu.trixcms.trixcore.common.fixtures.TranslatorFixture;
import org.junit.Test;

import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import static com.google.common.truth.Truth.assertThat;

public class SchedulerManagerTest {

    @Test
    public void schedule() throws InterruptedException {
        SchedulerManager manager = new SchedulerManager(TranslatorFixture.get());
        final CountDownLatch latch = new CountDownLatch(1);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                latch.countDown();
            }
        };

        manager.schedule(task, 100);
        Thread.sleep(100);

        assertThat(latch.getCount()).isEqualTo(0);
    }

    @Test
    public void resetScheduler() throws InterruptedException {
        SchedulerManager manager = new SchedulerManager(TranslatorFixture.get());
        final CountDownLatch latch = new CountDownLatch(1);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                latch.countDown();
            }
        };

        manager.schedule(task, 200);
        Thread.sleep(100);
        manager.resetScheduler();
        Thread.sleep(200);

        assertThat(latch.getCount()).isEqualTo(0);
    }
}
