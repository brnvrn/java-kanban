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
    private List<Integer> loadHistory;
    static List<Task> allTasks = new ArrayList<>();


    public FileBackedTasksManager(File saveFile) {
        this.saveFile = saveFile;
    }

    @Override
    public ArrayList<Task> getTasks() {
        return super.getTasks();
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
            throw new ManagerSaveException("Ошибка при записи данных: " + e.getMessage());
        }
    }

    public String toString(Task task) {
        return task.getId() + "," +
                task.getType() + "," +
                task.getName() + "," +
                task.getStatus() + "," +
                task.getDescription() + "," +
                task.getId();
    }

    public static Task fromString(String value) {
        final String[] values = value.split(",");
        Task task = new Task();
        task.setId(Integer.parseInt(values[0]));
        task.setType(TaskType.valueOf(values[1]));
        task.setName(values[2]);
        task.setStatus(TaskStatus.valueOf(values[3]));
        task.setDescription(values[4]);
        task.setEpic(values[5]);

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
        // Создаем новый список задач

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Пропустим первую строку с заголовком
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                Task task = fromString(line);
                // Добавляем задачу в список
                allTasks.add(task);
            }

            // Чтение истории просмотров
            String historyLine = reader.readLine();
            List<Integer> history = historyFromString(historyLine);
            taskManager.setHistory(history);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении данных: " + e.getMessage());
        }
        return taskManager;
    }

    public List<Task> getAllTasks() {
        return allTasks;
    }
    public List<Integer> getLoadHistory() {
        return this.loadHistory;
    }
    public void setHistory(List<Integer> history) {
        this.loadHistory = history;
    }
}

