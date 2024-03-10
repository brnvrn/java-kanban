package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;


public interface TaskManager {
    List<Task> getTasks();
    List<Subtask> getSubtasks();
    List<Epic> getEpics();
    void removeAllTasks();
    void removeAllEpics();
    void removeAllSubtasks();
    Task getTask(int id);
    Subtask getSubtask(int id);
    Epic getEpic(int id);
    int addNewTask(Task task);
    int addNewEpic(Epic epic);
    Integer addNewSubtask(Subtask subtask);
    void updateTask(Task task);
    void updateEpic(Epic epic);
    void updateSubtask(Subtask subtask);
    void deleteTask(int id);
    void deleteSubtask(int id);
    void deleteEpic(int id);
    List<Subtask> getSubtasksOfEpic(int epicId);
    List<Task> getHistory();
    List<Task> getPrioritizedTasks();
}