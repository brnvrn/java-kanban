package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history;
    public InMemoryHistoryManager() {
        history = new ArrayList<>();
    }
    public void add(Task task) {
        if (task != null) {
            if (history.size() >= 10) {
                history.remove(0);
            }
            history.add(task);
        }
    }
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }

}
