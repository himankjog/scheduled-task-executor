package task;

import com.sun.istack.internal.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.time.Instant;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

@Getter
@AllArgsConstructor
public class ScheduledTask {
    @NonNull private final Task task;
    @NonNull private Instant startTime;
    @Nullable private final Long period;
    @Nullable private final TimeUnit timeUnit;

    public ScheduledTask(@NonNull final Task task, @NonNull final Instant startTime) {
        this.task = task;
        this.startTime = startTime;
        this.period = 0L;
        this.timeUnit = TimeUnit.MILLISECONDS;
    }

    public long timeToRun() {
        return startTime.getEpochSecond() - Instant.now().getEpochSecond();
    }

    public boolean shouldRunNow() {
        return startTime.isBefore(Instant.now());
    }

    public void updateStartTime() {
        startTime = startTime.plusMillis(timeUnit.toMillis(period));
    }

    public boolean isRepeatableTask() {
        return period != 0L;
    }

    @Override
    public String toString() {
        return task.getName();
    }

    public static class ScheduledTaskComparator implements Comparator<ScheduledTask> {

        @Override
        public int compare(final ScheduledTask scheduledTask1, final ScheduledTask scheduledTask2) {
            return scheduledTask1.getStartTime().compareTo(scheduledTask2.getStartTime());
        }
    }
}
