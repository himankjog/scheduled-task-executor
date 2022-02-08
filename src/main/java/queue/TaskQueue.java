package queue;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import task.ScheduledTask;

import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Log4j2
public class TaskQueue {
    private static final ReentrantLock LOCK = new ReentrantLock();
    private static final Condition QUEUE_FULL = LOCK.newCondition();
    private static final Condition QUEUE_EMPTY = LOCK.newCondition();
    private static final Condition WAITING_FOR_TASK_EXECUTION_START_TIME = LOCK.newCondition();
    private static final AtomicBoolean shouldWait = new AtomicBoolean(true);

    private final PriorityQueue<ScheduledTask> queue;
    private final Integer maximumCapacity;

    public TaskQueue(@NonNull final Integer maximumCapacity) {
        queue = new PriorityQueue<>(maximumCapacity, new ScheduledTask.ScheduledTaskComparator());
        this.maximumCapacity = maximumCapacity;
    }

    public void publish(@NonNull final ScheduledTask task) {
        LOCK.lock();
        try {
            while (queue.size() == maximumCapacity) {
                QUEUE_FULL.await();
            }
            queue.add(task);
            QUEUE_EMPTY.signalAll();
            shouldWait.compareAndSet(true, false);
            WAITING_FOR_TASK_EXECUTION_START_TIME.signalAll();
            log.info("Published task: {} to the queue", task);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            LOCK.unlock();
        }
    }

    public ScheduledTask consume() throws InterruptedException {
        LOCK.lock();
        try {
            while (queue.isEmpty() || !shouldWait.compareAndSet(false, true)) {
                QUEUE_EMPTY.await();
            }
            ScheduledTask task = queue.peek();
            while (!task.shouldRunNow()) {
                task = queue.peek();
                WAITING_FOR_TASK_EXECUTION_START_TIME.await(task.timeToRun(), TimeUnit.MILLISECONDS);
            }
            queue.remove(task);
            shouldWait.getAndSet(false);
            QUEUE_FULL.signalAll();
            log.info("Consumed task: {} from the queue", task);
            return task;
        } finally {
            LOCK.unlock();
        }
    }
}
