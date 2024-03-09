package tests;

import manager.HttpTaskManager;
import manager.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.KVServer;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tasks.TaskType.*;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager>  {
    KVServer kvServer;


    private final Task task1 = new Task(1, TASK, "Task1", TaskStatus.NEW, "Des1", 60,
            LocalDateTime.of(2024, 1, 29, 15, 10));
    private final Epic epic1 = new Epic(1, EPIC, "Epic1", TaskStatus.IN_PROGRESS, "Desepic1");

    private final Subtask subtask1 = new Subtask(2, SUBTASK, "Subtask3", TaskStatus.IN_PROGRESS, "Dessubtask3",
            45, LocalDateTime.of(2024, 1, 15, 10, 11), 2);


    @BeforeEach
    void setUp() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        taskmanager = (HttpTaskManager) Managers.getDefault("http://localhost:8078");
    }

    @AfterEach
    void tearDown() {
        taskmanager.removeAllEpics();
        taskmanager.removeAllTasks();
        taskmanager.removeAllSubtasks();
        kvServer.stop();
    }
    @Test
    public void testSaveToServer() {
        taskmanager.addNewTask(task1);
        taskmanager.addNewEpic(epic1);
        taskmanager.addNewSubtask(subtask1);
        taskmanager.getTask(1);
        taskmanager.saveToServer();

        assertEquals(1,taskmanager.getTasks().size());
        assertEquals(1,taskmanager.getEpics().size());
        assertEquals(1,taskmanager.getSubtasks().size());
        assertEquals(1,taskmanager.getHistory().size());
        assertEquals(2, taskmanager.getPrioritizedTasks().size());
    }

    @Test
    public void testSaveToServerWithEmpty() {
        taskmanager.saveToServer();

        assertEquals(0,taskmanager.getTasks().size());
        assertEquals(0,taskmanager.getEpics().size());
        assertEquals(0,taskmanager.getSubtasks().size());
        assertEquals(0,taskmanager.getHistory().size());
        assertEquals(0, taskmanager.getPrioritizedTasks().size());
    }

    @Test
    public void testLoadFromServer() {
        taskmanager.addNewTask(task1);
        taskmanager.addNewEpic(epic1);
        taskmanager.addNewSubtask(subtask1);
        taskmanager.getTask(1);
        taskmanager.saveToServer();
       HttpTaskManager newHttpManager = (HttpTaskManager) Managers.getDefault("http://localhost:8078");

        newHttpManager.loadFromServer();

        assertEquals(1,newHttpManager.getTasks().size());
        assertEquals(1,newHttpManager.getEpics().size());
        assertEquals(1,newHttpManager.getSubtasks().size());
        assertEquals(1,newHttpManager.getHistory().size());
        assertEquals(2, newHttpManager.getPrioritizedTasks().size());
    }
    @Test
    public void testLoadFromServerWithEmpty() {
        taskmanager.saveToServer();
        HttpTaskManager newHttpManager = (HttpTaskManager) Managers.getDefault("http://localhost:8078");

        newHttpManager.loadFromServer();

        assertEquals(0,taskmanager.getTasks().size());
        assertEquals(0,taskmanager.getEpics().size());
        assertEquals(0,taskmanager.getSubtasks().size());
        assertEquals(0,taskmanager.getHistory().size());
        assertEquals(0, taskmanager.getPrioritizedTasks().size());
    }
}