package manager;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;


public class InMemoryTaskManager implements TaskManager {

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private int generatorId = 0;
    private final HistoryManager historyManager = Managers.getDefaultHistory();


    // a. Получение списка всех задач
    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    // Метод для получения списка всех подзадач
    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // Метод для получения списка всех эпиков
    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    // b. Удаление всех задач
    @Override
    public void removeAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
        historyManager.removeAllHistory();
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
        historyManager.removeAllHistory();
    }

    @Override
    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.cleanSubtaskId();
            updateEpicStatus(epic.getId());
        }
        subtasks.clear();
        historyManager.removeAllHistory();
    }

    // c. Получение задачи по идентификатору
    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    // Метод для получения эпика по идентификатору
    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    // Метод для получения подзадачи по идентификатору
    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    // d. Создание задачи
    @Override
    public int addNewTask(Task task) {
        int id = ++generatorId;
        task.setId(id);
        tasks.put(id, task);
        return task.getId();
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = ++generatorId;
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {

        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            System.out.println("Такого эпика нет: " + subtask.getEpicId());
            return null;
        }

        int id = ++generatorId;
        epic.addSubtaskId(id);
        subtask.setId(id);
        subtasks.put(id, subtask);


        updateEpicStatus(epic.getId());

        return subtask.getEpicId();
    }

    // e. Обновление задачи
    @Override
    public void updateTask(Task task) {
        int id = task.getId();
        if (tasks.containsKey(id)) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateEpic(Epic epic) {
        int id = epic.getId();
        if (epics.containsKey(id)) {
            Epic updEpic = epics.get(id);
            updEpic.setName(epic.getName());
            updEpic.setDescription(epic.getDescription());
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        int id = subtask.getId();
        if (subtasks.containsKey(id)) {
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(epic.getId());
        }
    }

    // f. Удаление задачи по идентификатору

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            ArrayList<Subtask> subtasksOfEpic = getSubtasksOfEpic(epic);
            epics.remove(id);
            for (Subtask subtask : subtasksOfEpic) {
                subtasks.remove(subtask.getId());
                deleteSubtask(subtask.getId());
            }
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            subtasks.remove(id);
            updateEpicStatus(epic.getId());
        }
        historyManager.remove(id);
    }

    //Дополнительные методы:
    //a. Получение списка всех подзадач определённого эпика.
    @Override
    public ArrayList<Subtask> getSubtasksOfEpic(Epic epic) {
        ArrayList<Subtask> subtasksOfEpic = new ArrayList<>();
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();

        for (Integer subtaskId : subtaskIds) {
            if (subtasks.containsKey(subtaskId)) {
                subtasksOfEpic.add(subtasks.get(subtaskId));
            }
        }
        return subtasksOfEpic;

    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    //Управление статусами
    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);

        if (epic.getSubtaskIds().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allDone = true;
        boolean inProgress = false;

        for (int subId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subId);
            TaskStatus subtaskStatus = subtask.getStatus();

            if (subtaskStatus == TaskStatus.IN_PROGRESS) {
                inProgress = true;
            } else if (subtaskStatus != TaskStatus.DONE) {
                allDone = false;
            }
        }

        if (inProgress) {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        } else if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.NEW);
        }
    }
}
