package manager;

import java.io.*;
import java.util.*;

import exceptions.ManagerSaveException;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;
import tasks.TaskType;

public class FileBackedTasksManager extends InMemoryTaskManager {

    public static void main(String[] args) {
        FileBackedTasksManager taskManager = new FileBackedTasksManager(new File("data.csv"));

        // Создание эпиков
        Epic epic1 = new Epic(TaskType.EPIC, "Написать курсовую работу", TaskStatus.NEW, "про компанию");

        final int epicId1 = taskManager.addNewEpic(epic1);

        // Создание подзадач
        Subtask subtask1 = new Subtask(TaskType.SUBTASK, "Написать конфликтную ситуацию", TaskStatus.NEW, "на рабочем месте", epicId1);
        taskManager.addNewSubtask(subtask1);

        // Создание задач
        Task task1 = new Task(TaskType.TASK, "Task 1", TaskStatus.NEW, "Description1");
        taskManager.addNewTask(task1);

        // Запрашиваем задачи, чтобы заполнить историю просмотра
        taskManager.getTask(3);
        taskManager.getEpic(1);
        taskManager.getSubtask(2);

        // Создаем новый менеджер из файла
        FileBackedTasksManager newTaskManager = FileBackedTasksManager.loadFromFile(new File("data.csv"));
        System.out.println("Восстановленный список задач: " + newTaskManager.getTasks());
        System.out.println("Восстановленный список эпиков: " + newTaskManager.getEpics());
        System.out.println("Восстановленный список подзадач: " + newTaskManager.getSubtasks());
        System.out.println("Восстановленная история просмотров: " + newTaskManager.getHistory());
        System.out.println(taskManager.equals(newTaskManager));
    }

    private final File saveFile;

    public FileBackedTasksManager(File saveFile) {
        this.saveFile = saveFile;
    }

    @Override
    public Task getTask(int id) {
        final Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        final Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        final Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public int addNewTask(Task task) {
        super.addNewTask(task);
        save();
        return task.getId();
    }

    @Override
    public int addNewEpic(Epic epic) {
        int result = super.addNewEpic(epic);
        save();
        return result;
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        save();
        return super.addNewSubtask(subtask);
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) {
            // Записываем заголовок файла
            writer.write("id,type,name,status,description,epic");
            writer.newLine();

            // Записываем задачи
            for (Task task : getTasks()) {
                writer.write(toString(task));
                writer.newLine();
            }
            for (Epic epic : getEpics()) {
                writer.write(toString(epic));
                writer.newLine();
            }
            for (Subtask subtask : getSubtasks()) {
                writer.write(toString(subtask));
                writer.newLine();
            }

            // Разделяем задачи и историю просмотров
            writer.newLine();

            // Записываем идентификаторы задач из истории просмотров
            for (Task task : super.getHistory()) {
                writer.write(task.getId() + ",");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи данных: " + e.getMessage(), e);
        }
    }

    public String toString(Task task) {
        StringBuilder taskString = new StringBuilder();
        taskString.append(task.getId()).append(",")
                .append(task.getType()).append(",")
                .append(task.getName()).append(",")
                .append(task.getStatus()).append(",")
                .append(task.getDescription());

        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            taskString.append(",").append(subtask.getEpicId());
        } else {
            taskString.append(",");
        }

        return taskString.toString();
    }

    public static Task fromString(String value) {
        final String[] values = value.split(",");
        int id = Integer.parseInt(values[0]);
        TaskType type = TaskType.valueOf(values[1]);
        String name = values[2];
        TaskStatus status = TaskStatus.valueOf(values[3]);
        String description = values[4];
        switch (type) {
            case EPIC:
              Epic epic = new Epic(id, type, name, status, description);
               return epic;
            case SUBTASK:
                Subtask subtask = new Subtask(id, type, name, status, description);
                subtask.setEpicId(Integer.parseInt(values[5]));
                return subtask;
            default:
                return new Task(id, type, name, status, description);
        }
    }


    public static String historyToString(HistoryManager manager) {
        StringBuilder historyString = new StringBuilder();
        for (Task taskId : manager.getHistory()) {
            historyString.append(taskId).append(",");
        }
        return historyString.toString();
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        if (value != null && !value.isEmpty()) {
            String[] values = value.split(",");
            for (String taskId : values) {
                history.add(Integer.parseInt(taskId));
            }
        }
        return history;
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager taskManager = new FileBackedTasksManager(file);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Пропустим первую строку с заголовком
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                Task task = fromString(line);

            switch (task.getType()) {
            case EPIC:
                taskManager.epics.put(task.getId(), (Epic) task);
                break;
            case SUBTASK:
                taskManager.subtasks.put(task.getId(), (Subtask) task);
                break;
            default:
                taskManager.tasks.put(task.getId(), task);
                }
            }
            // Чтение истории просмотров
            String historyLine = reader.readLine();
            taskManager.check(historyLine);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении данных: " + e.getMessage(), e);
        }

        return taskManager;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
       FileBackedTasksManager manager = (FileBackedTasksManager) obj;
        return Objects.equals(this.tasks, manager.tasks) &&
                Objects.equals(this.epics, manager.epics) &&
                Objects.equals(this.subtasks, manager.subtasks) &&
                Objects.equals(this.getHistory(), manager.getHistory());
    }

}

