package task;

import java.util.UUID;

public interface Task extends Runnable {
    UUID getTaskId();
    String getName();
    void execute();
}
