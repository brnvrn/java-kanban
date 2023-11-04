
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        // Создание задач
        Task task1 = new Task("Task 1", "Description1", TaskStatus.NEW);

        Task task2 = new Task("Task 2", "Description2", TaskStatus.IN_PROGRESS);

        // Создание эпиков
        Epic epic1 = new Epic("Написать курсовую работу", "по маркетингу", TaskStatus.NEW);
        Epic epic2 = new Epic("Смонтировать видеоролик", "про компанию", TaskStatus.IN_PROGRESS);
        final int epicId1 = taskManager.addNewEpic(epic1);
        final int epicId2 = taskManager.addNewEpic(epic2);
        // Создание подзадач
        Subtask subtask1 = new Subtask("Написать конфликтную ситуацию", "на рабочем месте", TaskStatus.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Оформить курсовую работу", "согласно ГОСТу", TaskStatus.NEW, epic1.getId());
        Subtask subtask3 = new Subtask("Отснять видеоматериал", "в офисе", TaskStatus.IN_PROGRESS, epic2.getId());

        // Добавление задач, эпиков и подзадач в менеджер
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);

        // Вывод списков задач, эпиков и подзадач
        System.out.println("Список задач:");
        for (Task task : taskManager.getTasks()) {
            System.out.println(task);
        }

        System.out.println("Список эпиков:");
        for (Epic epic : taskManager.getEpics()) {
            System.out.println(epic);
        }

        System.out.println("Список подзадач:");
        for (Subtask subtask : taskManager.getSubtasks()) {
            System.out.println(subtask);
        }

        // Изменение статусов объектов
        task1.setStatus(TaskStatus.DONE);
        subtask3.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);


        // Вывод обновленных статусов
        System.out.println("Обновленные статусы:");
        System.out.println("Задача 1: " + task1.getStatus());
        System.out.println("Подзадача 2: " + subtask2.getStatus());
        System.out.println("Подзадача 3: " + subtask3.getStatus());
        System.out.println("Эпик 2: " + epic2.getStatus());

        System.out.println("История просмотров:");
        System.out.println(taskManager.getEpic(6));
        System.out.println(taskManager.getSubtask(7));
        System.out.println(taskManager.getEpic(5));
        System.out.println(taskManager.getSubtask(9));
        System.out.println(taskManager.getTask(3));
        System.out.println(taskManager.getTask(4));
        System.out.println(taskManager.getSubtask(8));
        System.out.println(taskManager.getEpic(5));
        System.out.println(taskManager.getSubtask(7));
        System.out.println(taskManager.getHistory());

        // Удаление задачи и эпика
        taskManager.deleteTask(task2.getId());
        taskManager.deleteEpic(epic2.getId());
    }
}


