package tests;

import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.TaskType.*;

public abstract class TaskManagerTest <T extends TaskManager> {
    protected T taskmanager;

    private final Task task1 = new Task(1, TASK, "Task1", TaskStatus.NEW, "Des1", 60,
            LocalDateTime.of(2024, 1, 29, 15, 10));
    private final Epic epic1 = new Epic(1, EPIC, "Epic1", TaskStatus.IN_PROGRESS, "Desepic1");

    private final Subtask subtask1 = new Subtask(2, SUBTASK, "Subtask3", TaskStatus.IN_PROGRESS, "Dessubtask3",
            45, LocalDateTime.of(2024, 1, 15, 10, 11), epic1.getId());

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        taskmanager.removeAllTasks();
        taskmanager.removeAllEpics();
        taskmanager.removeAllSubtasks();
    }

    // a. Получение списка всех задач
    @Test
    public void testGetTasksWithDefaultBehavior() {
        taskmanager.addNewTask(task1);
        ArrayList<Task> tasks = new ArrayList<>(taskmanager.getTasks());

        assertTrue(tasks.contains(task1));
    }

    @Test
    public void testGetTasksWithEmptyTaskList() {
        ArrayList<Task> tasks = new ArrayList<>(taskmanager.getTasks());

        assertTrue(tasks.isEmpty());
    }

    @Test
    public void testGetEpicsWithDefaultBehavior() {
        taskmanager.addNewEpic(epic1);
        ArrayList<Epic> epics = new ArrayList<>(taskmanager.getEpics());

        assertTrue(epics.contains(epic1));
    }

    @Test
    public void testGetEpicsWithEmptyTaskList() {
        ArrayList<Epic> epics = new ArrayList<>(taskmanager.getEpics());

        assertTrue(epics.isEmpty());
    }

    @Test
    public void testGetSubtasksWithDefaultBehavior() {
     Epic epic2 = new Epic(1, SUBTASK, "Task2", TaskStatus.IN_PROGRESS, "Des2");
     Subtask subtask2 = new Subtask(5, SUBTASK, "Task3", TaskStatus.IN_PROGRESS, "Des3", 45,
             LocalDateTime.of(2024, 1, 15, 10, 11), epic2.getId());
        taskmanager.addNewEpic(epic2);

        assertNotNull(taskmanager.getEpic(epic2.getId()));

        taskmanager.addNewSubtask(subtask2);
        ArrayList<Subtask> subtasks = new ArrayList<>(taskmanager.getSubtasks());

        assertTrue(subtasks.contains(subtask2));
    }

    @Test
    public void testGetSubtasksWithEmptyTaskList() {
        ArrayList<Subtask> subtasks = new ArrayList<>(taskmanager.getSubtasks());

        assertTrue(subtasks.isEmpty());
    }

    @Test
    void testGetPrioritizedTasksStandardBehavior() {
        taskmanager.addNewTask(task1);
        taskmanager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(2, SUBTASK, "Subtask3", TaskStatus.IN_PROGRESS, "Dessubtask3",
                45, LocalDateTime.of(2024, 1, 15, 10, 11),2);
        taskmanager.addNewSubtask(subtask1);

        Set<Task> prioritizedTasks = taskmanager.getPrioritizedTasks();
        assertEquals(2, prioritizedTasks.size());
    }

    @Test
    void testGetPrioritizedTasksEmptyList() {
        Set<Task> prioritizedTasks = taskmanager.getPrioritizedTasks();
        assertTrue(prioritizedTasks.isEmpty());
    }

    // b. Удаление всех задач
    @Test
    void testRemoveAllTasks_defaultBehavior() {
        taskmanager.addNewTask(task1);
        taskmanager.addNewEpic(epic1);
        taskmanager.addNewSubtask(subtask1);

        taskmanager.removeAllTasks();

        assertTrue(taskmanager.getTasks().isEmpty());
        assertTrue(taskmanager.getEpics().isEmpty());
        assertTrue(taskmanager.getSubtasks().isEmpty());
        assertTrue(taskmanager.getPrioritizedTasks().isEmpty());
        assertTrue(taskmanager.getHistory().isEmpty());
    }

    @Test
    void testRemoveAllTasks_emptyTaskList() {
        taskmanager.removeAllTasks();

        assertTrue(taskmanager.getTasks().isEmpty());
        assertTrue(taskmanager.getEpics().isEmpty());
        assertTrue(taskmanager.getSubtasks().isEmpty());
        assertTrue(taskmanager.getPrioritizedTasks().isEmpty());
        assertTrue(taskmanager.getHistory().isEmpty());
    }

    @Test
    void testRemoveAllEpics_defaultBehavior() {
        taskmanager.addNewEpic(epic1);
        taskmanager.addNewSubtask(subtask1);

        taskmanager.removeAllEpics();

        assertTrue(taskmanager.getEpics().isEmpty());
        assertTrue(taskmanager.getSubtasks().isEmpty());
        assertTrue(taskmanager.getHistory().isEmpty());
    }

    @Test
    void testRemoveAllEpics_emptyTaskList() {
        taskmanager.removeAllEpics();

        assertTrue(taskmanager.getEpics().isEmpty());
        assertTrue(taskmanager.getSubtasks().isEmpty());
        assertTrue(taskmanager.getHistory().isEmpty());
    }

    @Test
    public void testRemoveAllSubtasks_defaultBehavior() {
        taskmanager.addNewEpic(epic1);
        taskmanager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask(5, SUBTASK, "Task3", TaskStatus.IN_PROGRESS, "Des3", 45,
                LocalDateTime.of(2024, 1, 19, 10, 11), epic1.getId());

        taskmanager.addNewSubtask(subtask2);
        taskmanager.removeAllSubtasks();

        // Проверка, что список айди сабтасок у эпиков очищен
        for (Epic epic : taskmanager.getEpics()) {
            assertTrue(epic.getSubtaskIds().isEmpty());
        }

        assertTrue(taskmanager.getSubtasks().isEmpty());
        assertTrue(taskmanager.getPrioritizedTasks().isEmpty());
        assertTrue(taskmanager.getHistory().isEmpty());
    }

    @Test
    void testRemoveAllSubtasks_emptyTaskList() {
        taskmanager.removeAllSubtasks();
        for (Epic epic : taskmanager.getEpics()) {
            assertTrue(epic.getSubtaskIds().isEmpty());
        }

        assertTrue(taskmanager.getSubtasks().isEmpty());
        assertTrue(taskmanager.getPrioritizedTasks().isEmpty());
        assertTrue(taskmanager.getHistory().isEmpty());
    }

    // c. Получение задачи по идентификатору
    @Test
    public void testGetTaskWithValidId() {
        taskmanager.addNewTask(task1);
        Task result = taskmanager.getTask(task1.getId());

        assertEquals(task1, result);
        assertTrue(taskmanager.getPrioritizedTasks().contains(task1));
        assertTrue(taskmanager.getHistory().contains(task1));
    }

    @Test
    public void testGetTaskWithEmptyTaskList() {
        Task result = taskmanager.getTask(task1.getId());

        assertNull(result);
        assertFalse(taskmanager.getHistory().contains(task1));
    }

    @Test
    public void testGetTaskWithInvalidId() {
        taskmanager.addNewTask(task1);

        Task result = taskmanager.getTask(999);

        assertNull(result);
        assertEquals(1,taskmanager.getPrioritizedTasks().size());
        assertFalse(taskmanager.getHistory().contains(999));
    }

    @Test
    public void testGetEpicsWithValidId() {
        taskmanager.addNewEpic(epic1);
        Epic result = taskmanager.getEpic(1);

        assertEquals(epic1, result);
        assertTrue(taskmanager.getHistory().contains(epic1));
    }

    @Test
    public void testGetEpicsWithEmptyEpicList() {
        Epic result = taskmanager.getEpic(2);

        assertNull(result);
        assertFalse(taskmanager.getHistory().contains(2));
    }

    @Test
    public void testGetEpicsWithInvalidId() {
        taskmanager.addNewEpic(epic1);
        Epic result = taskmanager.getEpic(999);

        assertNull(result);
        assertFalse(taskmanager.getHistory().contains(999));
    }
    @Test
    public void testGetSubtasksWithValidId() {
        Epic epic1 = new Epic(1, EPIC, "Task2", TaskStatus.IN_PROGRESS, "Des2");
        taskmanager.addNewEpic(epic1);
        Subtask subtask2 = new Subtask(5, SUBTASK, "Task3", TaskStatus.IN_PROGRESS, "Des3", 45,
                LocalDateTime.of(2024, 1, 15, 10, 11), epic1.getId());

        taskmanager.addNewSubtask(subtask2);
        Subtask result = taskmanager.getSubtask(subtask2.getId());

        assertEquals(subtask2, result);
        assertEquals(1,taskmanager.getPrioritizedTasks().size());
        assertTrue(taskmanager.getHistory().contains(subtask2));
    }

    @Test
    public void testGetSubtasksWithEmptySubtaskList() {
        Subtask result = taskmanager.getSubtask(3);

        assertNull(result);
        assertTrue(taskmanager.getPrioritizedTasks().isEmpty());
        assertFalse(taskmanager.getHistory().contains(3));
    }

    @Test
    public void testGetSubtasksWithInvalidId() {
        taskmanager.addNewEpic(epic1);
        taskmanager.addNewSubtask(subtask1);
        Subtask result = taskmanager.getSubtask(999);

        assertNull(result);
        assertEquals(1,taskmanager.getPrioritizedTasks().size());
        assertFalse(taskmanager.getHistory().contains(999));
    }

    // d. Создание задачи
    @Test
    public void testAddNewTaskWithValidTask() {
        Task task1 = new Task(1, TASK, "Task1", TaskStatus.NEW, "Des1", 60,
                LocalDateTime.of(2024, 1, 29, 15, 10));
        final int taskId = taskmanager.addNewTask(task1);
        final Task savedTask = taskmanager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task1, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskmanager.getTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(1,taskmanager.getPrioritizedTasks().size());
        assertEquals(task1, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void testAddNewTaskWithInvalidTaskId() {
        Task task2 = new Task(1, TASK, "Task1", TaskStatus.NEW, "Des1", 60,
                LocalDateTime.of(2024, 3, 29, 15, 10));

        final int taskId = taskmanager.addNewTask(task2);
        final Task savedTask = taskmanager.getTask(6);


        assertEquals(1, taskId);
        assertEquals(1,taskmanager.getPrioritizedTasks().size());
        assertNull(savedTask);
    }

    @Test
    public void testAddNewEpicWithValidEpic() {
        final int epicId = taskmanager.addNewEpic(epic1);
        final Epic savedEpic = taskmanager.getEpic(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic1, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskmanager.getEpics();

        assertNotNull(epics, "Эпики на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic1, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    public void testAddNewEpicWithInvalidEpicId() {
        Epic epic2 = new Epic(2, EPIC, "Task2", TaskStatus.IN_PROGRESS, "Des2");

        taskmanager.addNewEpic(epic2);
        final Epic savedEpic = taskmanager.getEpic(4);

        assertEquals(1, taskmanager.getEpics().size());
        assertNull(savedEpic);
    }

    @Test
    public void testAddNewSubtaskWithValidSubtask() {
        taskmanager.addNewEpic(epic1);
        taskmanager.addNewSubtask(subtask1);
        final Subtask savedSubTask = taskmanager.getSubtask(subtask1.getId());
        assertNotNull(savedSubTask, "Подзадача не найдена.");
        assertEquals(subtask1, savedSubTask, "Подзадачи не совпадают.");
        assertEquals(1,taskmanager.getPrioritizedTasks().size());

        final List<Subtask> subtasks = taskmanager.getSubtasks();

        assertNotNull(subtasks, "Подзадачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask1, subtasks.get(0), "Подзадачи не совпадают.");
        assertEquals(epic1.getStartTime(), subtask1.getStartTime());
        assertEquals(epic1.getDuration(), subtask1.getDuration());
        assertEquals(epic1.getEndTime(), subtask1.getEndTime());
    }

    @Test
    public void testAddNewSubtaskWithInvalidEpicId() {
        taskmanager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(2, SUBTASK, "Task3", TaskStatus.IN_PROGRESS, "Des3",
                45, LocalDateTime.of(2024, 1, 15, 10, 11), 2);
        Integer epicId = taskmanager.addNewSubtask(subtask1);

        assertNull(epicId);
        assertEquals(0,taskmanager.getPrioritizedTasks().size());
        assertFalse(taskmanager.getSubtasks().contains(subtask1));
    }

    // e. Обновление задачи
    @Test
    public void testUpdateTaskWithValidTask() {
        taskmanager.addNewTask(task1);
        task1.setDuration(20);
        taskmanager.updateTask(task1);

        Task result = taskmanager.getTask(1);

        assertEquals(task1, result);
        assertEquals(1,taskmanager.getPrioritizedTasks().size());
    }

    @Test
    public void testUpdateTaskWithEmptyTaskList() {
        taskmanager.updateTask(task1);

        Task result = taskmanager.getTask(1);

        assertNull(result);
        assertTrue(taskmanager.getPrioritizedTasks().isEmpty());
    }

    @Test
    public void testUpdateTaskWithInvalidTaskId() {
        taskmanager.addNewTask(task1);
        Task updatedTask = new Task(9, TASK, "Task1", TaskStatus.NEW, "Des1", 60,
                LocalDateTime.of(2024, 2, 29, 15, 10));
        taskmanager.updateTask(updatedTask);

        Task result = taskmanager.getTask(1);

        assertEquals(task1, result);
        assertEquals(1,taskmanager.getPrioritizedTasks().size());
    }

    @Test
    public void testUpdateEpicWithValidEpic() {
        taskmanager.addNewEpic(epic1);

        epic1.setName("Epic2");
        taskmanager.updateEpic(epic1);
        Epic savedEpic = taskmanager.getEpic(1);

        assertEquals(epic1, savedEpic);
    }

    @Test
    public void testUpdateEpicWithEmptyEpicList() {
        Epic updatedEpic =  new Epic(1, EPIC, "Epic21", TaskStatus.IN_PROGRESS, "Des2");
        taskmanager.updateEpic(updatedEpic);

        Epic savedEpic = taskmanager.getEpic(1);

        assertNull(savedEpic);
    }

    @Test
    public void testUpdateEpicWithInvalidEpicId() {
        taskmanager.addNewEpic(epic1);
        epic1.setName("Epic2");
        taskmanager.updateEpic(epic1);

        Epic savedEpic = taskmanager.getEpic(-1);

        assertNull(savedEpic);
    }

    @Test
    public void testUpdateSubtaskWithValidSubtask() {
        taskmanager.addNewEpic(epic1);
        taskmanager.addNewSubtask(subtask1);

        subtask1.setName("Updated Subtask");
        taskmanager.updateSubtask(subtask1);

        Subtask updatedSubtask = taskmanager.getSubtask(subtask1.getId());

        assertNotNull(updatedSubtask);
        assertEquals("Updated Subtask", updatedSubtask.getName());
        assertEquals(1,taskmanager.getPrioritizedTasks().size());
        assertEquals(epic1.getStartTime(), subtask1.getStartTime());
        assertEquals(epic1.getDuration(), subtask1.getDuration());
        assertEquals(epic1.getEndTime(), subtask1.getEndTime());
    }

    @Test
    public void testUpdateSubtaskWithEmptySubtaskList() {
        subtask1.setName("Updated Subtask");
        taskmanager.updateSubtask(subtask1);

        Subtask updatedSubtask = taskmanager.getSubtask(subtask1.getId());

        assertNull(updatedSubtask);
        assertTrue(taskmanager.getPrioritizedTasks().isEmpty());
    }

    @Test
    public void testUpdateSubtaskWithInvalidSubtaskId() {
        taskmanager.addNewEpic(epic1);
        taskmanager.addNewSubtask(subtask1);
        Subtask updatedSubtask = new Subtask(4, SUBTASK, "Task3", TaskStatus.IN_PROGRESS, "Des3",
                45, LocalDateTime.of(2024, 1, 10, 10, 11), epic1.getId());
        taskmanager.updateSubtask(updatedSubtask);

        Subtask result = taskmanager.getSubtask(2);

        assertEquals(subtask1, result);
        assertEquals(1,taskmanager.getPrioritizedTasks().size());
    }

    // f. Удаление задачи по идентификатору
    @Test
    public void testDeleteTaskWithValidTaskId() {
       taskmanager.addNewTask(task1);
        taskmanager.deleteTask(1);

        Task deletedTask = taskmanager.getTask(1);

        assertNull(deletedTask);
        assertTrue(taskmanager.getPrioritizedTasks().isEmpty());
        assertFalse(taskmanager.getHistory().contains(task1));
    }

    @Test
    public void testDeleteTaskWithEmptyTaskList() {
        taskmanager.deleteTask(1);

        Task deletedTask = taskmanager.getTask(1);

        assertNull(deletedTask);
        assertTrue(taskmanager.getPrioritizedTasks().isEmpty());
        assertFalse(taskmanager.getHistory().contains(1));
    }

    @Test
    public void testDeleteTaskWithInvalidTaskId() {
        taskmanager.addNewTask(task1);
        taskmanager.deleteTask(999);

        Task deletedTask = taskmanager.getTask(999);

        assertNull(deletedTask);
        assertFalse(taskmanager.getHistory().contains(999));
    }

    @Test
    public void testDeleteEpicWithValidEpicId() {
        taskmanager.addNewEpic(epic1);
        Subtask subtask2 = new Subtask(3, TASK, "Task3", TaskStatus.IN_PROGRESS, "Des3",
                45, LocalDateTime.of(2024, 9, 22, 9, 55), 1);

        taskmanager.addNewSubtask(subtask1);
        taskmanager.addNewSubtask(subtask2);
        taskmanager.deleteEpic(epic1.getId());

        assertNull(taskmanager.getEpic(epic1.getId()));
        assertNull(taskmanager.getSubtask(subtask1.getId()));
        assertNull(taskmanager.getSubtask(subtask2.getId()));
        assertFalse(taskmanager.getHistory().contains(epic1));
    }

    @Test
    public void testDeleteEpicWithEmptyEpicList() {
        taskmanager.deleteEpic(epic1.getId());

        assertTrue(taskmanager.getEpics().isEmpty());
        assertTrue(taskmanager.getSubtasks().isEmpty());
        assertFalse(taskmanager.getHistory().contains(epic1));
    }

    @Test
    public void testDeleteEpicWithInvalidEpicId() {
        taskmanager.addNewEpic(epic1);
        taskmanager.addNewSubtask(subtask1);
        taskmanager.deleteEpic(2);

        assertNotNull(taskmanager.getEpics());
        assertNotNull(taskmanager.getSubtasks());
        assertEquals(0,taskmanager.getHistory().size());
    }

    @Test
    public void testDeleteSubtaskWithValidId() {
        taskmanager.addNewEpic(epic1);
        taskmanager.addNewSubtask(subtask1);
        taskmanager.deleteSubtask(subtask1.getId());

        assertNull(taskmanager.getSubtask(subtask1.getId()));
        assertTrue(taskmanager.getPrioritizedTasks().isEmpty());
        assertFalse(taskmanager.getHistory().contains(subtask1));
    }

    @Test
    public void testDeleteSubtaskWithEmptySubtaskList() {
        taskmanager.deleteSubtask(3);

        assertTrue(taskmanager.getSubtasks().isEmpty());
        assertTrue(taskmanager.getPrioritizedTasks().isEmpty());
        assertFalse(taskmanager.getHistory().contains(subtask1));
    }

    @Test
    public void testDeleteSubtaskWithInvalidId() {
        taskmanager.deleteSubtask(100);

        assertEquals(0, taskmanager.getSubtasks().size());
        assertTrue(taskmanager.getPrioritizedTasks().isEmpty());
    }

    @Test
    public void testGetSubtasksOfEpicWithStandardBehavior() {
      Epic epic1 = new Epic(1, EPIC, "Epic1", TaskStatus.IN_PROGRESS, "Desepic1");

      Subtask subtask1 = new Subtask(2, SUBTASK, "Subtask3", TaskStatus.IN_PROGRESS, "Dessubtask3",
                45, LocalDateTime.of(2024, 1, 15, 10, 11), epic1.getId());
        Subtask subtask2 = new Subtask(3, TASK, "Task3", TaskStatus.IN_PROGRESS, "Des3",
                45, LocalDateTime.of(2024, 9, 22, 9, 55), 1);

        taskmanager.addNewEpic(epic1);
        taskmanager.addNewSubtask(subtask1);
        taskmanager.addNewSubtask(subtask2);

        List<Subtask> subtasksOfEpic = taskmanager.getSubtasksOfEpic(epic1);

        assertNotNull(subtasksOfEpic);
        assertEquals(2, subtasksOfEpic.size());
        assertTrue(subtasksOfEpic.contains(subtask1));
        assertTrue(subtasksOfEpic.contains(subtask2));
    }

    @Test
    public void testGetSubtasksOfEpicWithEmptySubtaskList() {
        Epic epic1 = new Epic(1, EPIC, "Epic1", TaskStatus.IN_PROGRESS, "Desepic1");
        taskmanager.addNewEpic(epic1);

        List<Subtask> subtasksOfEpic = taskmanager.getSubtasksOfEpic(epic1);

        assertNotNull(subtasksOfEpic);
        assertTrue(subtasksOfEpic.isEmpty());
    }

    @Test
    public void testUpdateEpicStatus_EmptySubtaskList() {
        Epic epic1 = new Epic(1, EPIC, "Epic1", TaskStatus.IN_PROGRESS, "Desepic1");
        taskmanager.addNewEpic(epic1);
        taskmanager.updateEpicStatus(epic1.getId());
        // Проверяем, что статус Epic установлен как NEW
        assertEquals(TaskStatus.NEW, epic1.getStatus());
    }

    @Test
    public void testUpdateEpicStatus_AllSubtasksNew() {
        Epic epic1 = new Epic(1, EPIC, "Epic1", TaskStatus.IN_PROGRESS, "Desepic1");
        Subtask subtask1 = new Subtask(2, SUBTASK, "Subtask3", TaskStatus.NEW, "Dessubtask3",
                45, LocalDateTime.of(2024, 1, 15, 10, 11), epic1.getId());
        Subtask subtask2 = new Subtask(3, TASK, "Task3", TaskStatus.NEW, "Des3",
                45, LocalDateTime.of(2024, 9, 22, 9, 55), 1);
        taskmanager.addNewEpic(epic1);
        taskmanager.addNewSubtask(subtask1);
        taskmanager.addNewSubtask(subtask2);
        assertEquals(TaskStatus.NEW, epic1.getStatus());
    }

    @Test
    public void testUpdateEpicStatus_AllSubtasksDone() {
        Epic epic1 = new Epic(1, EPIC, "Epic1", TaskStatus.IN_PROGRESS, "Desepic1");
        Subtask subtask1 = new Subtask(2, SUBTASK, "Subtask3", TaskStatus.DONE, "Dessubtask3",
                45, LocalDateTime.of(2024, 1, 15, 10, 11), epic1.getId());
        Subtask subtask2 = new Subtask(3, TASK, "Task3", TaskStatus.DONE, "Des3",
                45, LocalDateTime.of(2024, 9, 22, 9, 55), 1);
        taskmanager.addNewEpic(epic1);
        taskmanager.addNewSubtask(subtask1);
        taskmanager.addNewSubtask(subtask2);
        assertEquals(TaskStatus.DONE, epic1.getStatus());
    }

    @Test
    public void testUpdateEpicStatus_SubtasksNewAndDone() {
        Epic epic1 = new Epic(1, EPIC, "Epic1", TaskStatus.IN_PROGRESS, "Desepic1");
        Subtask subtask1 = new Subtask(2, SUBTASK, "Subtask3", TaskStatus.DONE, "Dessubtask3",
                45, LocalDateTime.of(2024, 1, 15, 10, 11), epic1.getId());
        Subtask subtask2 = new Subtask(3, TASK, "Task3", TaskStatus.NEW, "Des3",
                45, LocalDateTime.of(2024, 9, 22, 9, 55), 1);
        taskmanager.addNewEpic(epic1);
        taskmanager.addNewSubtask(subtask1);
        taskmanager.addNewSubtask(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus());
    }

    @Test
    public void testUpdateEpicStatus_SubtasksInProgress() {
        Epic epic1 = new Epic(1, EPIC, "Epic1", TaskStatus.NEW, "Desepic1");
        Subtask subtask1 = new Subtask(2, SUBTASK, "Subtask3", TaskStatus.IN_PROGRESS, "Dessubtask3",
                45, LocalDateTime.of(2024, 1, 15, 10, 11), epic1.getId());
        Subtask subtask2 = new Subtask(3, TASK, "Task3", TaskStatus.IN_PROGRESS, "Des3",
                45, LocalDateTime.of(2024, 9, 22, 9, 55), 1);
        taskmanager.addNewEpic(epic1);
        taskmanager.addNewSubtask(subtask1);
        taskmanager.addNewSubtask(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus());
    }
}

