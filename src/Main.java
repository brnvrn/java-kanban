
import manager.FileBackedTasksManager;
import tasks.*;

import java.io.File;


public class Main {

    public static void main(String[] args) {
        FileBackedTasksManager taskManager = new FileBackedTasksManager(new File("data.csv"));

        // Создание эпиков
        Epic epic1 = new Epic(TaskType.EPIC, "Написать курсовую работу", TaskStatus.NEW, "про компанию");

        final int epicId1 = taskManager.addNewEpic(epic1);

        // Создание подзадач
        Subtask subtask1 = new Subtask(TaskType.SUBTASK, "Написать конфликтную ситуацию", TaskStatus.NEW, "на рабочем месте", epicId1);
        taskManager.addNewSubtask(subtask1);

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
        System.out.println("Восстановленный список подзадач: " + newTaskManager.getHistory());

    }
}



