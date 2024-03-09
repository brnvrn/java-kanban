import manager.HttpTaskManager;
import manager.Managers;
import server.HttpTaskServer;
import server.KVServer;
import tasks.*;

import java.io.IOException;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        new KVServer().start();

        HttpTaskManager httpManager = (HttpTaskManager) Managers.getDefault("http://localhost:8078");
        new HttpTaskServer(httpManager).start();
        Epic epic1 = new Epic(TaskType.EPIC, "Написать курсовую работу", TaskStatus.NEW, "про компанию");

        final int epicId1 = httpManager.addNewEpic(epic1);

        // Создание подзадач
        Subtask subtask1 = new Subtask(TaskType.SUBTASK, "Написать конфликтную ситуацию", TaskStatus.NEW,"на рабочем месте", epicId1);
        subtask1.setDuration(60);
        subtask1.setStartTime(LocalDateTime.of(2024, 1, 29, 15, 5));
        httpManager.addNewSubtask(subtask1);
        subtask1.setDescription("22");
        httpManager.updateSubtask(subtask1);

        // Создание подзадачи, которая пересекается
        Subtask subtask2 = new Subtask(TaskType.SUBTASK, "Sub2", TaskStatus.NEW,"des2", epicId1);
        subtask2.setDuration(60);
        subtask2.setStartTime(LocalDateTime.of(2024, 1, 29, 15, 10));
        httpManager.addNewSubtask(subtask2);

        // Создание задач
        Task task1 = new Task(TaskType.TASK, "Task 1", TaskStatus.NEW, "Description1");
        task1.setDuration(40);
        task1.setStartTime(LocalDateTime.of(2024, 1, 20, 15, 50));
        httpManager.addNewTask(task1);


        // Запрашиваем задачи, чтобы заполнить историю просмотра
        httpManager.getTask(3);
        httpManager.getEpic(1);
        httpManager.getSubtask(2);
        httpManager.saveToServer();


        httpManager = (HttpTaskManager) Managers.getDefault("http://localhost:8078");
        httpManager.loadFromServer();

        System.out.println(httpManager.getTasks());
        System.out.println(httpManager.getHistory());
        System.out.println(httpManager.getPrioritizedTasks());
    }
}




