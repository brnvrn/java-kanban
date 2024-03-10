package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private static final Gson gson = new Gson();
    private final HttpServer server;


    public HttpTaskServer(TaskManager manager) throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", new TaskHandler(manager));
    }


    public void start() {
        System.out.println("Сервер запущен на порту " + PORT);
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Сервер остановлен");
    }

    static class TaskHandler implements HttpHandler {
        private final TaskManager manager;

        public TaskHandler(TaskManager manager) {
            this.manager = manager;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String response = "";
            int statusCode = 200;
            String requestMethod = exchange.getRequestMethod();
            String[] urlParts = path.split("/");

            if (requestMethod.equals("GET")) {
                switch (path) {
                    case "/tasks/":
                        handleGetPrioritizedTasks(exchange);
                        break;
                    case "/tasks/history/":
                        handleGetHistory(exchange);
                        break;
                    case "/tasks/subtasks/epic/":
                        handleGetSubtasksOfEpic(exchange);
                        break;
                    case "/tasks/task/":
                    case "/tasks/subtask/":
                    case "/tasks/epic/":
                        handleGet(exchange);
                        break;
                    default:
                        handleGetById(exchange);
                        break;
                }
            }

            if (requestMethod.equals("DELETE")) {
                switch (path) {
                    case "/tasks/task/":
                    case "/tasks/subtask/":
                    case "/tasks/epic/":
                        handleDelete(exchange);
                        break;
                    default:
                        handleDeleteById(exchange);
                        break;
                }
            }

            if (requestMethod.equals("POST") && urlParts[1].equals("tasks") && urlParts.length == 3) {
                switch (path) {
                    case "/tasks/task/":
                    case "/tasks/subtask/":
                    case "/tasks/epic/":
                        handlePost(exchange);
                        break;
                    default:
                        handleUpdate(exchange);
                        break;
                }
            }
        }


        public void handleGet(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String path = exchange.getRequestURI().getPath();
                String response = "";

                if ("/tasks/task/".equals(path)) {
                    response = gson.toJson(manager.getTasks());
                } else if ("/tasks/epic/".equals(path)) {
                    response = gson.toJson(manager.getEpics());
                } else if ("/tasks/subtask/".equals(path)) {
                    response = gson.toJson(manager.getSubtasks());
                } else {
                    response = "Неправильный эндпоинт";
                }

                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }


        public void handleGetById(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String path = exchange.getRequestURI().getPath();
                String query = exchange.getRequestURI().getQuery();
                String response = "";

                String[] pathSegments = path.split("/");
                if (pathSegments.length == 3 && query != null && query.startsWith("id=")) {
                    int id = Integer.parseInt(query.substring(3));
                    String entityType = pathSegments[2];
                    switch (entityType) {
                        case "task":
                            Task task = manager.getTask(id);
                            if (task != null) {
                                response = gson.toJson(task);
                            } else {
                                response = "Задача с id " + id + " не найдена";
                            }
                            break;
                        case "epic":
                            Epic epic = manager.getEpic(id);
                            if (epic != null) {
                                response = gson.toJson(epic);
                            } else {
                                response = "Эпик с id " + id + " не найден";
                            }
                            break;
                        case "subtask":
                            Subtask subtask = manager.getSubtask(id);
                            if (subtask != null) {
                                response = gson.toJson(subtask);
                            } else {
                                response = "Сабтаска с id " + id + " не найдена";
                            }
                            break;
                        default:
                            response = "Неправильный тип";
                    }
                } else {
                    response = "Неправильный эндпоинт";
                }

                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }

        public void handleGetSubtasksOfEpic(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String path = exchange.getRequestURI().getPath();
                String query = exchange.getRequestURI().getQuery();
                String response = "";

                String[] pathSegments = path.split("/");
                if (pathSegments.length == 4 && query != null && query.startsWith("id=")) {
                    int epicId = Integer.parseInt(query.substring(3));
                    String entityType = pathSegments[3];

                    if ("epic".equals(entityType)) {
                        ArrayList<Subtask> subtasksOfEpic = (ArrayList<Subtask>) manager.getSubtasksOfEpic(epicId);
                        response = subtasksOfEpic.toString();
                    } else {
                        response = "Неправильный эндпоинт";
                    }

                    exchange.sendResponseHeaders(200, response.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }
            }
        }

        public void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String path = exchange.getRequestURI().getPath();
                String response = "";
                int statusCode = 200;

                if ("/tasks/".equals(path)) {
                    List<Task> prioritizedTasks = manager.getPrioritizedTasks();
                    response = gson.toJson(prioritizedTasks);
                } else {
                    statusCode = 404;
                    response = "Неправильный эндпоинт";
                }

                exchange.sendResponseHeaders(statusCode, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }

        public void handleGetHistory(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String path = exchange.getRequestURI().getPath();
                String response = "";
                int statusCode = 200;

                if ("/tasks/history/".equals(path)) {
                    List<Task> history = manager.getHistory();
                    response = gson.toJson(history);
                } else {
                    statusCode = 404; // Not Found
                    response = "Неправильный эндпоинт";
                }

                exchange.sendResponseHeaders(statusCode, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }




        public void handleDelete(HttpExchange exchange) throws IOException {
            if ("DELETE".equals(exchange.getRequestMethod())) {
                String path = exchange.getRequestURI().getPath();
                String response = "";

                if ("/tasks/task/".equals(path)) {
                    manager.removeAllTasks();
                    response = "Все задачи удалены";
                } else if ("/tasks/epic/".equals(path)) {
                    manager.removeAllEpics();
                    response = "Все эпики удалены";
                } else if ("/tasks/subtask/".equals(path)) {
                    manager.removeAllSubtasks();
                    response = "Все сабтаски удалены";
                } else {
                    response = "Неправильный эндпоинт";
                }

                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }

        public void handleDeleteById(HttpExchange exchange) throws IOException {
            if ("DELETE".equals(exchange.getRequestMethod())) {
                String path = exchange.getRequestURI().getPath();
                String query = exchange.getRequestURI().getQuery();
                String response = "";

                String[] pathSegments = path.split("/");
                if (pathSegments.length == 3 && query != null && query.startsWith("id=")) {
                    int id = Integer.parseInt(query.substring(3));
                    String entityType = pathSegments[2];
                    switch (entityType) {
                            case "task":
                                manager.deleteTask(id);
                                response = "Задача с id " + id + " удалена";
                                break;
                            case "epic":
                                manager.deleteEpic(id);
                                response = "Эпик с id " + id + " удален";
                                break;
                            case "subtask":
                                manager.deleteSubtask(id);
                                response = "Сабтаска с id " + id + " удалена";
                                break;
                            default:
                                response = "Неправильный тип";
                                break;
                        }
                    } else {
                        response = "Неправильный URL формат";
                    }

                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }

        private void handlePost(HttpExchange exchange) throws IOException {
            String response = "";
            int statusCode = 200;
            try {
                InputStream is = exchange.getRequestBody();
                String reader = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                Task task;
                Subtask subtask;
                Epic epic;

                switch (exchange.getRequestURI().getPath()) {
                    case "/tasks/task/":
                        task = gson.fromJson(reader, Task.class);
                        manager.addNewTask(task);
                        response = "Задача создана";
                        break;
                    case "/tasks/epic/":
                        epic = gson.fromJson(reader, Epic.class);
                        manager.addNewEpic(epic);
                        response = "Эпик создан";
                        break;
                    case "/tasks/subtask/":
                        subtask = gson.fromJson(reader, Subtask.class);
                        manager.addNewSubtask(subtask);
                        response = "Сабтаска создана";
                        break;
                    default:
                        response = "Неправильный запрос";
                        statusCode = 400;
                        break;
                }
            } catch (JsonSyntaxException e) {
                response = "Неправильный формат JSON";
                statusCode = 400;
            }

            exchange.sendResponseHeaders(statusCode, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
        private void handleUpdate(HttpExchange exchange) throws IOException {
            String response = "";
            int statusCode = 200;
            try {
                InputStream is = exchange.getRequestBody();
                String reader = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                Task task;
                Subtask subtask;
                Epic epic;

                switch (exchange.getRequestURI().getPath()) {
                    case "/tasks/task":
                        task = gson.fromJson(reader, Task.class);
                        manager.updateTask(task);
                        response = "Задача обновлена";
                        break;
                    case "/tasks/epic":
                        epic = gson.fromJson(reader, Epic.class);
                        manager.updateEpic(epic);
                        response = "Эпик обновлен";
                        break;
                    case "/tasks/subtask":
                        subtask = gson.fromJson(reader, Subtask.class);
                        manager.updateSubtask(subtask);
                        response = "Сабтаска обновлена";
                        break;
                    default:
                        response = "Неправильный запрос";
                        statusCode = 400;
                        break;
                }
            } catch (JsonSyntaxException e) {
                response = "Неправильный формат JSON";
                statusCode = 400;
            }

            exchange.sendResponseHeaders(statusCode, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

    }
}



