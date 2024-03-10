package manager;

import exceptions.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class FileBackedTasksManager extends InMemoryTaskManager {

    public static void main(String[] args) {
        FileBackedTasksManager taskManager = new FileBackedTasksManager(new File("data.csv"));

        // Создание эпиков
        Epic epic1 = new Epic(TaskType.EPIC, "Написать курсовую работу", TaskStatus.NEW, "про компанию");

        final int epicId1 = taskManager.addNewEpic(epic1);

        // Создание подзадач
        Subtask subtask1 = new Subtask(TaskType.SUBTASK, "Написать конфликтную ситуацию", TaskStatus.NEW,"на рабочем месте", epicId1);
        subtask1.setDuration(60);
        subtask1.setStartTime(LocalDateTime.of(2024, 1, 29, 15, 5));
        taskManager.addNewSubtask(subtask1);
        subtask1.setDescription("22");
        taskManager.updateSubtask(subtask1);
        taskManager.setEpicTime(epic1);
        // Создание подзадачи, которая пересекается
        Subtask subtask2 = new Subtask(TaskType.SUBTASK, "Sub2", TaskStatus.NEW,"des2", epicId1);
        subtask2.setDuration(60);
        subtask2.setStartTime(LocalDateTime.of(2024, 1, 29, 15, 10));
        taskManager.addNewSubtask(subtask2);
        taskManager.setEpicTime(epic1);

        // Создание задач
        Task task1 = new Task(TaskType.TASK, "Task 1", TaskStatus.NEW, "Description1");
        task1.setDuration(40);
        task1.setStartTime(LocalDateTime.of(2024, 1, 20, 15, 50));
        taskManager.addNewTask(task1);


        // Запрашиваем задачи, чтобы заполнить историю просмотра
        taskManager.getTask(3);
        taskManager.getEpic(1);
        taskManager.getSubtask(2);
        System.out.println("Восстановленный список эпиков: " + taskManager.getEpics());
        System.out.println(taskManager.epics.get(subtask1.getEpicId()));
        System.out.println(taskManager.getSubtasksOfEpic(epic1.getId()));
        System.out.println(taskManager.getTasks());
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

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) {
            // Записываем заголовок файла
            writer.write("id,type,name,status,description, duration, startTime, epicEndTime, epic");
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

    private String toString(Task task) {
        StringBuilder taskString = new StringBuilder();
        taskString.append(task.getId()).append(",")
                .append(task.getType()).append(",")
                .append(task.getName()).append(",")
                .append(task.getStatus()).append(",")
                .append(task.getDescription()).append(",")
                        .append(task.getDuration()).append(",")
                        .append(task.getStartTime());
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            taskString.append(",").append(",").append(subtask.getEpicId());
        } else if (task instanceof Epic) {
            Epic epic = (Epic) task;
            taskString.append(",").append(epic.getEndTime());
        } else {
            taskString.append(",");
        }
        return taskString.toString();
    }

    private static Task fromString(String value) {
        final String[] values = value.split(",");
        int id = Integer.parseInt(values[0]);
        TaskType type = TaskType.valueOf(values[1]);
        String name = values[2];
        TaskStatus status = TaskStatus.valueOf(values[3]);
        String description = values[4];
        int duration = Integer.parseInt(values[5]);
        LocalDateTime startTime = null;
        if (!values[6].equals("null")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            startTime = LocalDateTime.parse(values[6], formatter);
        }
        switch (type) {
            case EPIC:
              Epic epic = new Epic(id, type, name, status, description);
                epic.setDuration(duration);
                epic.setStartTime(startTime);
                if (!values[7].equals("null")) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                    LocalDateTime endTime = LocalDateTime.parse(values[7], formatter);
                    epic.setEndTime(endTime);
                }
               return epic;
            case SUBTASK:
                Subtask subtask = new Subtask(id, type, name, status, description);
                subtask.setDuration(duration);
                subtask.setStartTime(startTime);
                subtask.setEpicId(Integer.parseInt(values[8]));
                return subtask;
            default:
                Task task =  new Task(id, type, name, status, description);
            task.setDuration(duration);
            task.setStartTime(startTime);
            return task;
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager taskManager = new FileBackedTasksManager(file);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Пропустим первую строку с заголовком
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                Task task = fromString(line);
            if (task.getId() > taskManager.generatorId) {
                taskManager.generatorId = task.getId();
            }
            switch (task.getType()) {
                case EPIC:
                    taskManager.epics.put(task.getId(), (Epic) task);
                    break;
                case SUBTASK:
                    Subtask sub = (Subtask) task;
                    Epic epic = taskManager.epics.get(sub.getEpicId());
                        if (epic != null) {
                            epic.addSubtaskId(sub.getId());
                            taskManager.subtasks.put(task.getId(), sub);
                        taskManager.prioritizedTasks.add(sub);
                    }
                    break;
                default:
                        taskManager.tasks.put(task.getId(), task);
                        taskManager.prioritizedTasks.add(task);
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
                Objects.equals(this.getHistory(), manager.getHistory()) &&
                Objects.equals(this.generatorId, manager.generatorId) &&
                Objects.equals(this.prioritizedTasks, manager.prioritizedTasks);
    }
}

