package task;

import lombok.extern.log4j.Log4j2;

import java.util.Random;
import java.util.UUID;

@Log4j2
public class DataFilterTask extends AbstractTask {
    public DataFilterTask(final UUID taskId, final String name) {
        super(taskId, name);
    }

    @Override
    public void execute() {
        log.info("Executing Data Filtering task: {}", getName());

        try {
            Thread.sleep(new Random().nextInt(5) * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("Done executing Data Filtering task: {}", getName());
    }

    @Override
    public void run() {
        execute();
    }

    @Override
    public String toString() {
        return getName();
    }
}
