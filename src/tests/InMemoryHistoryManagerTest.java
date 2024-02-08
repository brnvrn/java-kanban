package tests;

import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.TaskType.TASK;


class InMemoryHistoryManagerTest {
    protected HistoryManager historyManager;
    private final Task task1 = new Task(1, TASK, "Task1", TaskStatus.NEW, "Des1");
    private final Epic epic1 = new Epic(2, TASK, "Task2", TaskStatus.IN_PROGRESS, "Des2");
    private final Subtask subtask1 = new Subtask(3, TASK, "Task3", TaskStatus.IN_PROGRESS, "Des3", epic1.getId());


    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @AfterEach
    void tearDown() {
        historyManager.removeAllHistory();
    }

    @Test
    void testAddWithEmptyHistory() {
        // Проверка добавления задачи в пустую историю
        historyManager.add(task1);
        assertTrue(historyManager.getHistory().contains(task1));
    }

    @Test
    void testAddWithDuplicateTask() {
        historyManager.add(task1);
        // Дублирование задачи
        historyManager.add(task1);

        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    public void testRemoveFromEmptyHistory() {
        historyManager.remove(1);

        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    public void testRemoveDuplicate() {
        historyManager.add(task1);
        historyManager.add(task1);

        historyManager.remove(1);

        assertEquals(0, historyManager.getHistory().size());
    }

    @Test
    public void testRemoveFromHistory() {
        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(subtask1);

        historyManager.remove(2);

        assertEquals(2, historyManager.getHistory().size());
        assertFalse(historyManager.getHistory().contains(epic1));

        historyManager.remove(1);

        assertEquals(1, historyManager.getHistory().size());
        assertFalse(historyManager.getHistory().contains(task1));

        historyManager.remove(3);

        assertTrue(historyManager.getHistory().isEmpty());
        assertFalse(historyManager.getHistory().contains(subtask1));
    }

    @Test
    void testRemoveAllHistoryWithEmptyHistory() {
        historyManager.removeAllHistory();
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void testRemoveAllHistoryWithDuplicates() {
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.removeAllHistory();

        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void testGetHistoryWithEmptyHistory() {
        // Проверка получения истории из пустой истории задач
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }
    @Test
    void testGetHistoryWithDuplicateTasks() {
        // Проверка получения истории с дублированными задачами
        Task task2 = new Task(4, TASK, "Task2", TaskStatus.NEW, "Des2");
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size());
        assertTrue(history.contains(task1));
        assertTrue(history.contains(task2));
    }

    @Test
    void testGetHistoryAfterRemovingTasks() {
        // Проверка получения истории после удаления задач
        Task task2 = new Task(4, TASK, "Task2", TaskStatus.NEW, "Des2");
        Task task3 = new Task(5, TASK, "Task3", TaskStatus.NEW, "Des3");
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task1.getId());
        historyManager.remove(task2.getId());
        historyManager.remove(task3.getId());

        List<Task> history = historyManager.getHistory();

        assertTrue(history.isEmpty());
    }
}


