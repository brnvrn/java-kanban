package manager;

import java.util.List;

import tasks.Task;


public interface HistoryManager {
    void add(Task task);

    void remove(int id);

    void removeAllHistory();

    List<Task> getHistory();
}
