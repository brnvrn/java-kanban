
import manager.FileBackedTasksManager;
import tasks.*;

import java.io.File;


public class Main {

    public static void main(String[] args) {
        FileBackedTasksManager taskManager = new FileBackedTasksManager(new File("data.csv"));
        Task task1 = new Task(TaskType.TASK, "Task 1", TaskStatus.NEW, "Description1");
        Task task2 = new Task(TaskType.TASK, "Task 2", TaskStatus.IN_PROGRESS, "Description2");

        // Создание эпиков
        Epic epic1 = new Epic(TaskType.EPIC, "Написать курсовую работу", TaskStatus.NEW, "про компанию");
        Epic epic2 = new Epic(TaskType.EPIC, "Смонтировать видеоролик", TaskStatus.IN_PROGRESS, "про компанию");

        final int epicId1 = taskManager.addNewEpic(epic1);
        final int epicId2 = taskManager.addNewEpic(epic2);

        // Создание подзадач
        Subtask subtask1 = new Subtask(TaskType.SUBTASK,"Написать конфликтную ситуацию", TaskStatus.NEW, "на рабочем месте", epicId1);
        Subtask subtask2 = new Subtask(TaskType.SUBTASK, "Оформить курсовую работу", TaskStatus.NEW, "согласно ГОСТу", epicId1);
        Subtask subtask3 = new Subtask(TaskType.SUBTASK, "Отснять видеоматериал", TaskStatus.IN_PROGRESS, "в офисе", epicId2);

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
        System.out.println("Восстановленный список задач: " + newTaskManager.getLoadedTasks()); // Выводит список задач
        System.out.println("Восстановленная история просмотра задач: " + newTaskManager.getLoadHistory()); // Выводит идентификаторы задач из истории просмотров



    }
}


