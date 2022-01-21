package resources;

import lombok.NonNull;
import queue.TaskQueue;
import task.ScheduledTask;
import task.Task;
import utils.CancellationToken;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ScheduledTaskExecutor {
    private final CancellationToken cancellationToken;
    private final TaskQueue queue;
    private final List<ScheduledTaskRunnable> runnables;
    private final Integer numberOfThreads;
    private final Integer taskBacklogLimit;

    public ScheduledTaskExecutor(@NonNull final Integer numberOfThreads, @NonNull final Integer taskBacklogLimit) {
        cancellationToken = new CancellationToken();
        queue = new TaskQueue(taskBacklogLimit);
        runnables = new ArrayList<>(numberOfThreads);
        this.numberOfThreads = numberOfThreads;
        this.taskBacklogLimit = taskBacklogLimit;

        for (int threadCount = 1; threadCount <= numberOfThreads; ++threadCount) {
            runnables.add(new ScheduledTaskRunnable(queue));
        }

        for (ScheduledTaskRunnable runnable: runnables) {
            new Thread(runnable).start();
        }
    }

    public synchronized void schedule(@NonNull final Task task, @NonNull final long delay, @NonNull final TimeUnit timeUnit) {
        final Instant startTime = getTaskStartTime(delay, timeUnit);
        final ScheduledTask scheduledTask = new ScheduledTask(task, startTime);
        queue.publish(scheduledTask);
    }

    public synchronized void schedule(@NonNull final Task task, @NonNull final long delay,
                                      @NonNull final long period, @NonNull final TimeUnit timeUnit) {
        final Instant startTime = getTaskStartTime(delay, timeUnit);
        final ScheduledTask scheduledTask = new ScheduledTask(task, startTime, period, timeUnit);
        queue.publish(scheduledTask);
    }

    private Instant getTaskStartTime(@NonNull final long delay, @NonNull final TimeUnit timeUnit) {
        return Instant.now().plusMillis(timeUnit.toMillis(delay));
    }
}
