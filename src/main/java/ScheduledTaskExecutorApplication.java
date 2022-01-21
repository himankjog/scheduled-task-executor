import resources.ScheduledTaskExecutor;
import task.DataFilterTask;
import task.DataGatherTask;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ScheduledTaskExecutorApplication {
    public static void main(String[] args) {
        final DataGatherTask dataGatherTask = new DataGatherTask(UUID.randomUUID(), "Gather Data Task 1");
        final DataFilterTask dataFilterTask = new DataFilterTask(UUID.randomUUID(), "Data Filter Task 1");
        final DataFilterTask dataFilterTask2 = new DataFilterTask(UUID.randomUUID(), "Data Filter Task 2");

        final ScheduledTaskExecutor taskExecutor = new ScheduledTaskExecutor(5, 10);

        taskExecutor.schedule(dataGatherTask, 5L, TimeUnit.SECONDS);
        taskExecutor.schedule(dataFilterTask, 10L, 7L, TimeUnit.SECONDS);
        taskExecutor.schedule(dataFilterTask2, 17L, 15L, TimeUnit.SECONDS);
    }
}
