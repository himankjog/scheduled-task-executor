package resources;


import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import queue.TaskQueue;
import task.ScheduledTask;
import utils.CancellationToken;

@Log4j2
@Getter
public class ScheduledTaskRunnable implements Runnable {
    private final CancellationToken cancellationToken;
    private final TaskQueue queue;

    @Setter
    private Thread thread;

    public ScheduledTaskRunnable(@NonNull final TaskQueue queue) {
        this.queue = queue;
        cancellationToken = new CancellationToken();
    }

    @Override
    public void run() {
        setThread(Thread.currentThread());

        while (!cancellationToken.isCancelled() && !Thread.currentThread().isInterrupted()) {
            try {
                final ScheduledTask task = queue.consume();
                task.getTask().run();
                if (task.isRepeatableTask()) {
                    task.updateStartTime();
                    queue.publish(task);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void cancel() {
        cancellationToken.cancel();
        log.error("Cancelled ScheduledTaskRunnable");
    }

    public void stop() {
        cancellationToken.cancel();
        thread.interrupt();
        log.error("Stopped ScheduledTaskRunnable");
    }
}
