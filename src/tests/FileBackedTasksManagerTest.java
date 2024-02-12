package tests;

import manager.FileBackedTasksManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tasks.TaskType.EPIC;
import static tasks.TaskType.TASK;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    private File saveFile;

    @BeforeEach
    void setUp() {
        saveFile = new File("testData.csv");
        taskmanager = new FileBackedTasksManager(saveFile);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testSaveEmptyTaskList() {
        // Проверка сохранения пустого списка задач
        taskmanager.save();

        assertTrue(saveFile.exists());
        assertEquals(75, saveFile.length()); // записывается заголовок файла в 62 символа
    }

    @Test
    void testSaveEpicWithoutSubtasks() {
        Epic epic1 = new Epic(1, EPIC, "Epic1", TaskStatus.IN_PROGRESS, "Desepic1");
        taskmanager.addNewEpic(epic1);
        taskmanager.save();

        assertTrue(saveFile.exists());
        assertEquals(1, taskmanager.getEpics().size());
        assertTrue(taskmanager.getTasks().isEmpty());
        assertTrue(taskmanager.getSubtasks().isEmpty());
        assertTrue(taskmanager.getPrioritizedTasks().isEmpty());
        assertTrue(taskmanager.getHistory().isEmpty());
    }

    @Test
    void testSaveEmptyHistory() {
        Task task1 = new Task(1, TASK, "Task1", TaskStatus.NEW, "Des1", 60,
                LocalDateTime.of(2024, 1, 29, 15, 10));
        taskmanager.addNewTask(task1);
        taskmanager.save();

        assertTrue(saveFile.exists());
        assertEquals(1, taskmanager.getTasks().size());
        assertTrue(taskmanager.getEpics().isEmpty());
        assertTrue(taskmanager.getSubtasks().isEmpty());
        assertEquals(1,taskmanager.getPrioritizedTasks().size());
        assertTrue(taskmanager.getHistory().isEmpty());
    }

    @Test
    void testLoadFromFileWithEmptyTasks() {
        FileBackedTasksManager tasksManager = new FileBackedTasksManager(saveFile);
        tasksManager.save();
        FileBackedTasksManager newTasksManager = FileBackedTasksManager.loadFromFile(saveFile);

        assertTrue(saveFile.exists());
        assertTrue(newTasksManager.getTasks().isEmpty());
        assertTrue(newTasksManager.getEpics().isEmpty());
        assertTrue(newTasksManager.getSubtasks().isEmpty());
        assertTrue(newTasksManager.getPrioritizedTasks().isEmpty());
        assertTrue(newTasksManager.getHistory().isEmpty());
    }

    @Test
    void testLoadFromFileWithEpicWithoutSubtasks() {
        FileBackedTasksManager tasksManager = new FileBackedTasksManager(saveFile);
        Epic epic1 = new Epic(1, EPIC, "Epic1", TaskStatus.IN_PROGRESS, "Desepic1");
        tasksManager.addNewEpic(epic1);
        tasksManager.save();
        FileBackedTasksManager newTasksManager = FileBackedTasksManager.loadFromFile(saveFile);

        assertEquals(1, newTasksManager.getEpics().size());
        assertTrue(newTasksManager.getTasks().isEmpty());
        assertTrue(newTasksManager.getSubtasks().isEmpty());
        assertTrue(newTasksManager.getPrioritizedTasks().isEmpty());
    }

    @Test
    void testLoadFromFileWithEmptyHistory() {
        FileBackedTasksManager tasksManager = new FileBackedTasksManager(saveFile);
        Task task1 = new Task(1, TASK, "Task1", TaskStatus.NEW, "Des1", 60,
                LocalDateTime.of(2024, 1, 29, 15, 10));
        tasksManager.addNewTask(task1);
        tasksManager.save();
        FileBackedTasksManager newTasksManager = FileBackedTasksManager.loadFromFile(saveFile);

        assertEquals(1, newTasksManager.getTasks().size());
        assertTrue(newTasksManager.getEpics().isEmpty());
        assertTrue(newTasksManager.getSubtasks().isEmpty());
        assertEquals(1, newTasksManager.getPrioritizedTasks().size());
        assertTrue(newTasksManager.getHistory().isEmpty());
    }
}
