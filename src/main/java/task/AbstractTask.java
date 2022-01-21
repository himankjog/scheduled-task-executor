package task;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public abstract class AbstractTask implements Task {
    private final UUID taskId;
    private final String name;
}
