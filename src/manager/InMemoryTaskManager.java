package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId));
    protected int generatorId = 0;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

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
        if (isValidate(task)) {
            int id = ++generatorId;
            task.setId(id);
            tasks.put(id, task);
            prioritizedTasks.add(task);
            return task.getId();
        } else {
            System.out.println("Задача пересекается с другими задачами или подзадачами");
            return -1;
        }
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
    if (isValidate(subtask)) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic == null) {
                System.out.println("Такого эпика нет: " + subtask.getEpicId());
                return null;
            }

            int id = ++generatorId;
            epic.addSubtaskId(id);
            subtask.setId(id);
            subtasks.put(id, subtask);
            prioritizedTasks.add(subtask);


            updateEpicStatus(epic.getId());

            return subtask.getEpicId();
    } else {
        System.out.println("Сабтаск пересекается с другими задачами или подзадачами");
        return -1;
    }
    }

    // e. Обновление задачи
    @Override
    public void updateTask(Task task) {
        if (isValidate(task)) {
            int id = task.getId();
            if (tasks.containsKey(id)) {
                tasks.put(task.getId(), task);
                prioritizedTasks.add(task);
            }
            } else {
                System.out.println("Задача пересекается с другими задачами или подзадачами");
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
        if(isValidate(subtask)) {
        Epic epic = epics.get(subtask.getEpicId());
        int id = subtask.getId();
        if (subtasks.containsKey(id)) {
            subtasks.put(subtask.getId(), subtask);
            prioritizedTasks.add(subtask);
            updateEpicStatus(epic.getId());
        }
        } else {
            System.out.println("Сабтаск пересекается с другими задачами или подзадачами");
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
            epic.cleanSubtaskId();
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
    public void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);

        if (epic.getSubtaskIds().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

       TaskStatus status = null;

        for (int subId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subId);

            if (status == null) {
                status = subtask.getStatus();
                continue;
            }
            if (status.equals(subtask.getStatus()) && !status.equals((TaskStatus.IN_PROGRESS))) {
                continue;
            }
            epic.setStatus(TaskStatus.IN_PROGRESS);
            return;
            }
            epic.setStatus(status);
        }

    protected void check(String historyLine) {
        if (historyLine == null) {
            return; // Если файл пуст, ничего не считываем
        }
        for (String idString : historyLine.split(",")) {
            try {
                int id = Integer.parseInt(idString.trim());
                Task task = tasks.get(id);
                if (task == null) task = subtasks.get(id);
                if (task == null) task = epics.get(id);
                if (task != null) historyManager.add(task);
            } catch (NumberFormatException e) {
                System.out.println("Некорректное значение id: " + idString);
            }
        }
    }

    public boolean isValidate(Task task) {
            LocalDateTime taskStartTime = task.getStartTime();
            LocalDateTime taskEndTime = task.getEndTime();

            // Проверяем пересечение с другими задачами
            for (Task existingTask : prioritizedTasks) {
                if (existingTask.getId() != task.getId()) { // Исключаем текущую задачу из проверки
                    LocalDateTime existingTaskStartTime = existingTask.getStartTime();
                    LocalDateTime existingTaskEndTime = existingTask.getEndTime();

                    if (taskStartTime.isBefore(existingTaskEndTime) && taskEndTime.isAfter(existingTaskStartTime)) {
                        // Задачи пересекаются по времени выполнения
                        return false;
                    }
                }
            }

            // Проверяем пересечение с подзадачами
            for (Task subtask : prioritizedTasks) {
                LocalDateTime subtaskStartTime = subtask.getStartTime();
                LocalDateTime subtaskEndTime = subtask.getEndTime();

                if (taskStartTime.isBefore(subtaskEndTime) && taskEndTime.isAfter(subtaskStartTime)) {
                    // Задача пересекается с подзадачей по времени выполнения
                    return false;
                }
            }
            return true; // Все проверки пройдены успешно
    }

    public void setEpicTime(Epic epic) {
        List<Subtask> subtasks = getSubtasksOfEpic(epic);
        if (!subtasks.isEmpty()) {
            LocalDateTime earliestStartTime = subtasks.get(0).getStartTime();
            LocalDateTime latestEndTime = subtasks.get(0).getEndTime();
            for (Subtask subtask : subtasks) {
                if (subtask.getStartTime().isBefore(earliestStartTime)) {
                    earliestStartTime = subtask.getStartTime();
                }
                if (subtask.getEndTime().isAfter(latestEndTime)) {
                    latestEndTime = subtask.getEndTime();
                }
            }
            Duration duration = Duration.between(earliestStartTime, latestEndTime);
            int durationMinutes = (int) duration.toMinutes();
            epic.setStartTime(earliestStartTime);
            epic.setEndTime(latestEndTime);
            epic.setDuration(durationMinutes);
        }
    }
}
