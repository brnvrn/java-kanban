
import manager.FileBackedTasksManager;
import tasks.*;

import java.io.File;


public class Main {

    public static void main(String[] args) {
        FileBackedTasksManager taskManager = new FileBackedTasksManager(new File("data.csv"));
        Task task1 = new Task("Task 1", "Description1", TaskStatus.NEW, TaskType.TASK);

        Task task2 = new Task("Task 2", "Description2", TaskStatus.IN_PROGRESS, TaskType.TASK);

        // Создание эпиков
        Epic epic1 = new Epic("Написать курсовую работу", "по маркетингу", TaskStatus.NEW, TaskType.EPIC);
        Epic epic2 = new Epic("Смонтировать видеоролик", "про компанию", TaskStatus.IN_PROGRESS, TaskType.EPIC);
        final int epicId1 = taskManager.addNewEpic(epic1);
        final int epicId2 = taskManager.addNewEpic(epic2);

        // Создание подзадач
        Subtask subtask1 = new Subtask("Написать конфликтную ситуацию", "на рабочем месте", TaskStatus.NEW, TaskType.SUBTASK, epicId1);
        Subtask subtask2 = new Subtask("Оформить курсовую работу", "согласно ГОСТу", TaskStatus.NEW, TaskType.SUBTASK, epicId1);
        Subtask subtask3 = new Subtask("Отснять видеоматериал", "в офисе", TaskStatus.IN_PROGRESS, TaskType.SUBTASK, epicId2);

        // Создаем менеджер задач



        // Добавляем задачи в менеджер
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);

        // Запрашиваем задачи, чтобы заполнить историю просмотра
        taskManager.getTask(3);
        taskManager.getEpic(1);
        taskManager.getEpic(2);
        taskManager.getSubtask(5);
        taskManager.getSubtask(6);

        // Сохраняем текущее состояние менеджера в файл
        taskManager.save();

        // Создаем новый менеджер из файла
        FileBackedTasksManager newTaskManager = FileBackedTasksManager.loadFromFile(new File("data.csv"));



        // Проверяем, что все задачи, эпики и подзадачи восстановлены
        System.out.println("Восстановленный список задач: " + newTaskManager.getAllTasks()); // Выводит список задач
        System.out.println("Восстановленная история просмотра задач: " + newTaskManager.getLoadHistory()); // Выводит идентификаторы задач из истории просмотров



    }
}


