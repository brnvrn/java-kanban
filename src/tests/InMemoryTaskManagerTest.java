package tests;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.TaskType.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void setUp() {
        taskmanager = new InMemoryTaskManager();
    }

    @AfterEach
    void tearDown() {
    }
    @Test
    void testSetEpicTimeWithStandardBehavior() {
        Epic epic1 = new Epic(1, EPIC, "Epic1", TaskStatus.IN_PROGRESS, "Desepic1");
        Subtask subtask1 = new Subtask(2, SUBTASK, "Subtask1", TaskStatus.NEW, "Dessubtask1",
                10, LocalDateTime.of(2024, 1, 15, 10, 11), epic1.getId());
        Subtask subtask2 = new Subtask(3, SUBTASK, "Subtask2", TaskStatus.IN_PROGRESS, "Dessubtask2",
                35, LocalDateTime.of(2024, 1, 15, 22, 20), epic1.getId());
        Subtask subtask3 = new Subtask(4, SUBTASK, "Subtask3", TaskStatus.IN_PROGRESS, "Dessubtask3",
                60, LocalDateTime.of(2024, 1, 15, 17, 30), epic1.getId());
        taskmanager.addNewEpic(epic1);
        taskmanager.addNewSubtask(subtask1);
        taskmanager.addNewSubtask(subtask2);
        taskmanager.addNewSubtask(subtask3);
        subtask3.getEndTime();
        taskmanager.setEpicTime(epic1);
        // Проверка, что время начала и конца эпика установлены правильно
        assertEquals(LocalDateTime.of(2024, 1, 15, 10, 11), epic1.getStartTime());
        assertEquals(LocalDateTime.of(2024, 1, 15, 22, 55), epic1.getEndTime());
        assertEquals(105, epic1.getDuration());
    }

    @Test
    void testSetEpicTimeWithEmptySubtasks() {
        Epic epic1 = new Epic(1, EPIC, "Epic1", TaskStatus.IN_PROGRESS, "Desepic1");
        taskmanager.addNewEpic(epic1);
        taskmanager.setEpicTime(epic1);
        // Проверка, что время начала и конца эпика остались неизменными
        assertNull(epic1.getStartTime());
        assertNull(epic1.getEndTime());
        assertEquals(0, epic1.getDuration());
    }

    @Test
    void testIsValidateWithStandardBehavior() {
        Task task1 = new Task(1, TASK, "Task1", TaskStatus.NEW, "Des1", 60,
                LocalDateTime.of(2024, 1, 29, 15, 10));
        Task task2 = new Task(2, TASK, "Task1", TaskStatus.NEW, "Des1", 55,
                LocalDateTime.of(2024, 1, 29, 15, 10));
        Task task3 = new Task(3, TASK, "Task1", TaskStatus.NEW, "Des1", 15,
                LocalDateTime.of(2024, 1, 11, 15, 10));
        taskmanager.addNewTask(task1);
        taskmanager.addNewTask(task2);
        taskmanager.addNewTask(task3);
        assertEquals(2, taskmanager.getTasks().size());
        assertFalse(taskmanager.getTasks().contains(task2)); // Проверяем, что первая задача валидна
        assertTrue(taskmanager.getTasks().contains(task1)); // Проверяем, что вторая задача пересекается с первой
        assertTrue(taskmanager.getTasks().contains(task3)); // Проверяем, что третья задача валидна
    }
}







