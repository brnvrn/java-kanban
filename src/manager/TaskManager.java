package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import java.util.ArrayList;
import java.util.List;


public interface TaskManager {
    ArrayList<Task> getTasks();
    ArrayList<Subtask> getSubtasks();
    ArrayList<Epic> getEpics();
    void removeAllTasks();
    void removeAllEpics();
    void removeAllSubtasks(Subtask subtask);
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
    ArrayList<Subtask> getSubtasksOfEpic(Epic epic);
    List<Task> getHistory();
}