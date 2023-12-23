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
    private final File saveFile;
    private List<Task> loadedTasks;
    private List<Integer> loadHistory;


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
        save();
        return super.addNewTask(task);

    }

    @Override
    public int addNewEpic(Epic epic) {
        save();
        return super.addNewEpic(epic);
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

    public void save() {
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
                .append(task.getDescription()).append(",")
                .append(task.getEpicId());
        return taskString.toString();
    }

    public static Task fromString(String value) {
        final String[] values = value.split(",");
        Task task = null;
        if (values[1].equals(TaskType.EPIC.toString())) {
            task = new Epic();
            task.setEpicId(Integer.parseInt(values[0]));
        } else if (values[1].equals(TaskType.SUBTASK.toString())) {
            task = new Subtask();
            task.setEpicId(Integer.parseInt(values[5]));
        } else {
            task = new Task();
        }
        task.setId(Integer.parseInt(values[0]));
        task.setType(TaskType.valueOf(values[1]));
        task.setName(values[2]);
        task.setStatus(TaskStatus.valueOf(values[3]));
        task.setDescription(values[4]);

        return task;
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

            List<Task> tasks = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                Task task = fromString(line);
                tasks.add(task);
            }

            taskManager.setTasks(tasks);

            // Чтение истории просмотров
            String historyLine = reader.readLine();
            List<Integer> history = historyFromString(historyLine);
            taskManager.setHistory(history);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении данных: " + e.getMessage(), e);
        }

        return taskManager;
    }

    private void setTasks(List<Task> tasks) {
        this.loadedTasks = tasks;
    }
    public List<Task> getLoadedTasks() {
        return loadedTasks;
    }
    public List<Integer> getLoadHistory() {
        return this.loadHistory;
    }

    private void setHistory(List<Integer> history) {
        this.loadHistory = history;
    }
}

