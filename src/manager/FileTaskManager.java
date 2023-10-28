package manager;

import java.util.ArrayList;
import java.util.HashMap;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class FileTaskManager implements TaskManager {

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int generatorId = 0;

    // a. Получение списка всех задач
    @Override
    public ArrayList<Task> getTasks(){
        ArrayList<Task> taskArrayList = new ArrayList<>(tasks.values());
        return taskArrayList;
    }
    // Метод для получения списка всех подзадач
    @Override
    public ArrayList<Subtask> getSubtasks() {
        ArrayList<Subtask> subtaskArrayList = new ArrayList<>(subtasks.values());
        return subtaskArrayList;
    }

    // Метод для получения списка всех эпиков
    @Override
    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> epicArrayList = new ArrayList<>(epics.values());
        return epicArrayList;
    }

    // b. Удаление всех задач
    @Override
    public void removeAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }
    @Override
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }
    @Override
    public void removeAllSubtasks(Subtask subtask) {
        Epic epic = getEpic(subtask.getEpicId());
        subtasks.clear();
        updateEpicStatus(epic.getId());
    }

    // c. Получение задачи по идентификатору
    @Override
    public Task getTask(int id) {
            return tasks.get(id);
    }

    // Метод для получения эпика по идентификатору
    @Override
    public Epic getEpic(int id) {
            return epics.get(id);
    }
    // Метод для получения подзадачи по идентификатору
    @Override
    public Subtask getSubtask(int id) {
            return subtasks.get(id);
    }

    // d. Создание задачи
    @Override
    public int addNewTask(Task task) {
        int id = ++generatorId;
        task.setId(id);
        tasks.put(id, task);
        return  id;
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

        Epic epic = getEpic(subtask.getEpicId());
        if (epic == null) {
            System.out.println("Такого эпика нет: " + subtask.getEpicId());
            return null;
        }

        int id = ++generatorId;
        epic.addSubtaskId(id);
        subtask.setId(id);
        subtasks.put(id, subtask);


        updateEpicStatus(epic.getId());

        return id;
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
        Epic epic = getEpic(subtask.getEpicId());
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
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = getEpic(id);
        if (epic != null) {
            ArrayList<Subtask> subtasksOfEpic = getSubtasksOfEpic(epic);
            epics.remove(id);
            for (Subtask subtask : subtasksOfEpic) {
                subtasks.remove(subtask.getId());
            }
        }
    }
    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = getEpic(subtask.getEpicId());
        subtasks.remove(id);
        updateEpicStatus(epic.getId());
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
    //Управление статусами
    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);

        if (epic.getSubtaskIds().isEmpty()) {
            epic.setStatus("NEW");
            return;
        }

        boolean allDone = true;
        boolean inProgress = false;

        for (int subId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subId);
            String subtaskStatus = subtask.getStatus();

            if (subtaskStatus.equals("IN_PROGRESS")) {
                inProgress = true;
            } else if (!subtaskStatus.equals("DONE")) {
                allDone = false;
            }
        }

        if (inProgress) {
            epic.setStatus("IN_PROGRESS");
        } else if (allDone) {
            epic.setStatus("DONE");
        } else {
            epic.setStatus("NEW");
        }
    }
}
