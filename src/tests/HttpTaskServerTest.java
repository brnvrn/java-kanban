package tests;

import com.google.gson.Gson;
import manager.HttpTaskManager;
import manager.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import server.KVServer;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tasks.TaskType.*;

class HttpTaskServerTest {
KVServer kvServer;
HttpTaskManager httpManager;
HttpTaskServer httpTaskServer;
    private final Task task1 = new Task(1, TASK, "Task1", TaskStatus.NEW, "Des1", 60,
            LocalDateTime.of(2024, 1, 29, 15, 10));
    private final Epic epic1 = new Epic(1, EPIC, "Epic1", TaskStatus.IN_PROGRESS, "Desepic1");

    private final Subtask subtask1 = new Subtask(2, SUBTASK, "Subtask3", TaskStatus.IN_PROGRESS, "Dessubtask3",
            45, LocalDateTime.of(2024, 1, 15, 10, 11), epic1.getId());
    HttpClient client = HttpClient.newHttpClient();

    @BeforeEach
    void setUp() throws IOException {
       kvServer = new KVServer();
       kvServer.start();
       httpManager = (HttpTaskManager) Managers.getDefault("http://localhost:8078");
       httpTaskServer = new HttpTaskServer(httpManager);
       httpTaskServer.start();
    }

    @AfterEach
    void tearDown() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    @Test
    void testHandleGetTasksEndpoint() throws Exception {
        httpManager.addNewTask(task1);
        httpManager.save();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем, что статус ответа равен 200 (OK)
        assertEquals(200, response.statusCode());

        // Проверяем, что ответ содержит ожидаемый JSON с задачами
        String expectedResponse = "[{\"id\":1,\"name\":\"Task1\",\"status\":\"NEW\",\"description\":\"Des1\"," +
                "\"type\":\"TASK\",\"duration\":60,\"startTime\":{\"date\":{\"year\":2024,\"month\":1,\"day\":29}," +
                "\"time\":{\"hour\":15,\"minute\":10,\"second\":0,\"nano\":0}}}]";
        assertEquals(expectedResponse, response.body());
    }

    @Test
    void testHandleGetEpicsEndpoint() throws Exception {
        httpManager.addNewEpic(epic1);
        httpManager.save();

        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем, что статус ответа равен 200 (OK)
        assertEquals(200, response.statusCode());

        // Проверяем, что ответ содержит ожидаемый JSON с эпиками
        String expectedResponse = "[{\"subtaskIds\":[],\"id\":1,\"name\":\"Epic1\",\"status\":\"IN_PROGRESS\"," +
                "\"description\":\"Desepic1\",\"type\":\"EPIC\",\"duration\":0}]";
        assertEquals(expectedResponse, response.body());
    }

    @Test
    void testHandleGetSubtasksEndpoint() throws Exception {
        httpManager.addNewEpic(epic1);
        httpManager.addNewSubtask(subtask1);
        httpManager.save();

        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем, что статус ответа равен 200 (OK)
        assertEquals(200, response.statusCode());

        // Проверяем, что ответ содержит ожидаемый JSON с эпиками
        String expectedResponse = "[{\"epicId\":1,\"id\":2,\"name\":\"Subtask3\",\"status\":\"IN_PROGRESS\"," +
                "\"description\":\"Dessubtask3\",\"type\":\"SUBTASK\",\"duration\":45,\"startTime\"" +
                ":{\"date\":{\"year\":2024,\"month\":1,\"day\":15},\"time\":{\"hour\":10,\"minute\":11,\"second\":0," +
                "\"nano\":0}}}]";
        assertEquals(expectedResponse, response.body());
    }

    @Test
    void testHandleGetTasksEndpointEmpty() throws Exception {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем, что статус ответа равен 200 (OK)
        assertEquals(200, response.statusCode());

        // Проверяем, что ответ содержит пустой список задач
        String expectedResponse = "[]";
        assertEquals(expectedResponse, response.body());
    }

    @Test
    void testHandleGetEpicsEndpointEmpty() throws Exception {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем, что статус ответа равен 200 (OK)
        assertEquals(200, response.statusCode());

        // Проверяем, что ответ содержит пустой список эпиков
        String expectedResponse = "[]";
        assertEquals(expectedResponse, response.body());
    }

    @Test
    void testHandleGetSubtasksEndpointEmpty() throws Exception {
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем, что статус ответа равен 200 (OK)
        assertEquals(200, response.statusCode());

        // Проверяем, что ответ содержит пустой список подзадач
        String expectedResponse = "[]";
        assertEquals(expectedResponse, response.body());
    }

    @Test
    void testHandleGetPrioritizedTasksEndpoint() throws Exception {
        httpManager.addNewTask(task1);
        httpManager.save();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем, что статус ответа равен 200 (OK)
        assertEquals(200, response.statusCode());

        // Проверяем, что ответ содержит ожидаемый JSON с задачами
        String expectedResponse = "[{\"id\":1,\"name\":\"Task1\",\"status\":\"NEW\",\"description\":\"Des1\"," +
                "\"type\":\"TASK\",\"duration\":60,\"startTime\":{\"date\":{\"year\":2024,\"month\":1,\"day\":29}," +
                "\"time\":{\"hour\":15,\"minute\":10,\"second\":0,\"nano\":0}}}]";
        assertEquals(expectedResponse, response.body());
    }

    @Test
    void testHandleGetPrioritizedTasksEndpointEmpty() throws Exception {
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем, что статус ответа равен 200 (OK)
        assertEquals(200, response.statusCode());

        // Проверяем, что ответ содержит пустой список подзадач
        String expectedResponse = "[]";
        assertEquals(expectedResponse, response.body());
    }

    @Test
    void testHandleGetHistoryEndpoint() throws Exception {
        httpManager.addNewTask(task1);
        httpManager.getTask(1);
        httpManager.save();
        URI url = URI.create("http://localhost:8080/tasks/history/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем, что статус ответа равен 200 (OK)
        assertEquals(200, response.statusCode());

        // Проверяем, что ответ содержит ожидаемый JSON с задачами
        String expectedResponse = "[{\"id\":1,\"name\":\"Task1\",\"status\":\"NEW\",\"description\":\"Des1\"," +
                "\"type\":\"TASK\",\"duration\":60,\"startTime\":{\"date\":{\"year\":2024,\"month\":1,\"day\":29}," +
                "\"time\":{\"hour\":15,\"minute\":10,\"second\":0,\"nano\":0}}}]";
        assertEquals(expectedResponse, response.body());
    }

    @Test
    void testHandleGetHistoryEndpointEmpty() throws Exception {
        URI url = URI.create("http://localhost:8080/tasks/history/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем, что статус ответа равен 200 (OK)
        assertEquals(200, response.statusCode());

        // Проверяем, что ответ содержит пустой список подзадач
        String expectedResponse = "[]";
        assertEquals(expectedResponse, response.body());
    }
    @Test
    void testHandleGetTaskByIdEndpoint() throws Exception {
        httpManager.addNewTask(task1);
        httpManager.save();
        URI url = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем, что статус ответа равен 200 (OK)
        assertEquals(200, response.statusCode());


        // Проверяем, что ответ содержит ожидаемый JSON с задачами
        String expectedResponse = "{\"id\":1,\"name\":\"Task1\",\"status\":\"NEW\",\"description\":\"Des1\",\"type\":" +
                "\"TASK\",\"duration\":60,\"startTime\":{\"date\":{\"year\":2024,\"month\":1,\"day\":29},\"time\":" +
                "{\"hour\":15,\"minute\":10,\"second\":0,\"nano\":0}}}";
        assertEquals(expectedResponse, response.body());
    }

    @Test
    void testHandleGetTaskByIdEndpointEmpty() throws Exception {
        URI url = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем, что статус ответа равен 200 (OK)
        assertEquals(200, response.statusCode());

        // Проверяем, что ответ содержит пустой список подзадач
        String expectedResponse = "Задача с id 1 не найдена";
        assertEquals(expectedResponse, response.body());
    }

    @Test
    void testHandleGetTaskByIdEndpointWithInvalidId() throws Exception {
        httpManager.addNewTask(task1);
        httpManager.save();
        URI url = URI.create("http://localhost:8080/tasks/task?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем, что статус ответа равен 200 (OK)
        assertEquals(200, response.statusCode());

        // Проверяем, что ответ содержит пустой список подзадач
        String expectedResponse = "Задача с id 2 не найдена";
        assertEquals(expectedResponse, response.body());
    }

    @Test
    void testHandleGetEpicByIdEndpoint() throws Exception {
        httpManager.addNewEpic(epic1);
        httpManager.save();
        URI url = URI.create("http://localhost:8080/tasks/epic?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем, что статус ответа равен 200 (OK)
        assertEquals(200, response.statusCode());


        // Проверяем, что ответ содержит ожидаемый JSON с задачами
        String expectedResponse = "{\"subtaskIds\":[],\"id\":1,\"name\":\"Epic1\",\"status\":\"IN_PROGRESS\"," +
                "\"description\":\"Desepic1\",\"type\":\"EPIC\",\"duration\":0}";
        assertEquals(expectedResponse, response.body());
    }

    @Test
    void testHandleGetEpicByIdEndpointEmpty() throws Exception {
        URI url = URI.create("http://localhost:8080/tasks/epic?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем, что статус ответа равен 200 (OK)
        assertEquals(200, response.statusCode());

        // Проверяем, что ответ содержит пустой список подзадач
        String expectedResponse = "Эпик с id 1 не найден";
        assertEquals(expectedResponse, response.body());
    }

    @Test
    void testHandleGetEpicByIdEndpointWithInvalidId() throws Exception {
        httpManager.addNewEpic(epic1);
        httpManager.save();
        URI url = URI.create("http://localhost:8080/tasks/epic?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем, что статус ответа равен 200 (OK)
        assertEquals(200, response.statusCode());

        // Проверяем, что ответ содержит пустой список подзадач
        String expectedResponse = "Эпик с id 2 не найден";
        assertEquals(expectedResponse, response.body());
    }

    @Test
    void testHandleGetSubtaskByIdEndpoint() throws Exception {
        httpManager.addNewEpic(epic1);
        httpManager.addNewSubtask(subtask1);
        httpManager.save();
        URI url = URI.create("http://localhost:8080/tasks/subtask?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем, что статус ответа равен 200 (OK)
        assertEquals(200, response.statusCode());


        // Проверяем, что ответ содержит ожидаемый JSON с задачами
        String expectedResponse = "{\"epicId\":1,\"id\":2,\"name\":\"Subtask3\",\"status\":\"IN_PROGRESS\"," +
                "\"description\":\"Dessubtask3\",\"type\":\"SUBTASK\",\"duration\":45,\"startTime\":{\"date\":" +
                "{\"year\":2024,\"month\":1,\"day\":15},\"time\":{\"hour\":10,\"minute\":11,\"second\":0,\"nano\":0}}}";
        assertEquals(expectedResponse, response.body());
    }

    @Test
    void testHandleGetSubtaskByIdEndpointEmpty() throws Exception {
        URI url = URI.create("http://localhost:8080/tasks/subtask?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем, что статус ответа равен 200 (OK)
        assertEquals(200, response.statusCode());

        // Проверяем, что ответ содержит пустой список подзадач
        String expectedResponse = "Сабтаска с id 1 не найдена";
        assertEquals(expectedResponse, response.body());
    }

    @Test
    void testHandleGetSubtaskByIdEndpointEmptyWithInvalidId() throws Exception {
        httpManager.addNewEpic(epic1);
        httpManager.addNewSubtask(subtask1);
        httpManager.save();
        URI url = URI.create("http://localhost:8080/tasks/subtask?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем, что статус ответа равен 200 (OK)
        assertEquals(200, response.statusCode());

        // Проверяем, что ответ содержит пустой список подзадач
        String expectedResponse = "Сабтаска с id 3 не найдена";
        assertEquals(expectedResponse, response.body());
    }

    @Test
    void testHandleGetSubtaskOfEpicEndpoint() throws Exception {
        httpManager.addNewEpic(epic1);
        httpManager.addNewSubtask(subtask1);
        httpManager.save();
        URI url = URI.create("http://localhost:8080/tasks/subtasks/epic/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем, что статус ответа равен 200 (OK)
        assertEquals(200, response.statusCode());

        // Проверяем, что ответ содержит ожидаемый JSON с задачами
        String expectedResponse = "[Subtask{id=2, name='Subtask3', status=IN_PROGRESS, description='Dessubtask3'," +
                " duration=45, startTime=2024-01-15T10:11}]";
        assertEquals(expectedResponse, response.body());
    }

    @Test
    void testHandleDeleteTaskEndpoint() throws Exception {
        httpManager.addNewTask(task1);
        httpManager.save();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем, что статус ответа равен 200 (OK)
        assertEquals(200, response.statusCode());

        // Проверяем, что ответ содержит ожидаемый JSON с задачами
        String expectedResponse = "Все задачи удалены";
        assertEquals(expectedResponse, response.body());
    }

    @Test
    void testHandleDeleteEpicEndpoint() throws Exception {
        httpManager.addNewEpic(epic1);
        httpManager.save();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем, что статус ответа равен 200 (OK)
        assertEquals(200, response.statusCode());

        // Проверяем, что ответ содержит ожидаемый JSON с задачами
        String expectedResponse = "Все эпики удалены";
        assertEquals(expectedResponse, response.body());
    }

    @Test
    void testHandleDeleteSubtaskEndpoint() throws Exception {
        httpManager.addNewEpic(epic1);
        httpManager.addNewSubtask(subtask1);
        httpManager.save();
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем, что статус ответа равен 200 (OK)
        assertEquals(200, response.statusCode());

        // Проверяем, что ответ содержит ожидаемый JSON с задачами
        String expectedResponse = "Все сабтаски удалены";
        assertEquals(expectedResponse, response.body());
    }

    @Test
    void testHandleDeleteTaskByIdEndpoint() throws Exception {
        httpManager.addNewTask(task1);
        httpManager.save();
        URI url = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем, что статус ответа равен 200 (OK)
        assertEquals(200, response.statusCode());


        // Проверяем, что ответ содержит ожидаемый JSON с задачами
        String expectedResponse = "Задача с id 1 удалена";
        assertEquals(expectedResponse, response.body());
    }

    @Test
    void testHandleDeleteEpicByIdEndpoint() throws Exception {
        httpManager.addNewEpic(epic1);
        httpManager.save();
        URI url = URI.create("http://localhost:8080/tasks/epic?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем, что статус ответа равен 200 (OK)
        assertEquals(200, response.statusCode());


        // Проверяем, что ответ содержит ожидаемый JSON с задачами
        String expectedResponse = "Эпик с id 1 удален";
        assertEquals(expectedResponse, response.body());
    }

    @Test
    void testHandlePostTaskEndpont() throws Exception {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        Gson gson = new Gson();
        String json = gson.toJson(task1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Проверяем, что статус ответа равен 200 (OK)
        assertEquals(200, response.statusCode());


        // Проверяем, что ответ содержит ожидаемый JSON с задачами
        String expectedResponse = "Задача создана";
        assertEquals(expectedResponse, response.body());
    }

    @Test
    void testHandlePostEpicEndpont() throws Exception {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Gson gson = new Gson();
        String json = gson.toJson(epic1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Проверяем, что статус ответа равен 200 (OK)
        assertEquals(200, response.statusCode());


        // Проверяем, что ответ содержит ожидаемый JSON с задачами
        String expectedResponse = "Эпик создан";
        assertEquals(expectedResponse, response.body());
    }

    @Test
    void testHandlePostSubtaskEndpont() throws Exception {
        httpManager.addNewEpic(epic1);
        httpManager.addNewSubtask(subtask1);
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        Gson gson = new Gson();
        String json = gson.toJson(subtask1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Проверяем, что статус ответа равен 200 (OK)
        assertEquals(200, response.statusCode());


        // Проверяем, что ответ содержит ожидаемый JSON с задачами
        String expectedResponse = "Сабтаска создана";
        assertEquals(expectedResponse, response.body());
    }

    @Test
    void testHandleUpdateTaskEndpont() throws Exception {
        URI url = URI.create("http://localhost:8080/tasks/task");
        Gson gson = new Gson();
        String json = gson.toJson(task1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Проверяем, что статус ответа равен 200 (OK)

        Task task1 = new Task(1, TASK, "Task2", TaskStatus.NEW, "Des1", 60,
                LocalDateTime.of(2024, 1, 29, 15, 10));
        assertEquals(200, response.statusCode());
        String json1 = gson.toJson(task1);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(json1);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(body1).build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());

        // Проверяем, что ответ содержит ожидаемый JSON с задачами
        String expectedResponse = "Задача обновлена";
        assertEquals(expectedResponse, response1.body());
    }

    @Test
    void testHandleUpdateEpicEndpont() throws Exception {
        URI url = URI.create("http://localhost:8080/tasks/epic");
        Gson gson = new Gson();
        String json = gson.toJson(epic1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Проверяем, что статус ответа равен 200 (OK)

        Epic epic1 = new Epic(1, EPIC, "Epic1", TaskStatus.IN_PROGRESS, "Desepic1");

        assertEquals(200, response.statusCode());
        String json1 = gson.toJson(epic1);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(json1);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(body1).build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());

        // Проверяем, что ответ содержит ожидаемый JSON с задачами
        String expectedResponse = "Эпик обновлен";
        assertEquals(expectedResponse, response1.body());
    }

    @Test
    void testHandleUpdateSubtaskEndpont() throws Exception {
        httpManager.addNewEpic(epic1);
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        Gson gson = new Gson();
        String json = gson.toJson(subtask1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Subtask subtask1 = new Subtask(2, SUBTASK, "Subtask4", TaskStatus.IN_PROGRESS, "Dessubtask3",
                45, LocalDateTime.of(2024, 1, 15, 10, 11), 1);
        String json1 = gson.toJson(subtask1);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(json1);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(body1).build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());

        // Проверяем, что статус ответа равен 200 (OK)
        assertEquals(200, response1.statusCode());


        // Проверяем, что ответ содержит ожидаемый JSON с задачами
        String expectedResponse = "Сабтаска обновлена";
        assertEquals(expectedResponse, response.body());
    }
}

