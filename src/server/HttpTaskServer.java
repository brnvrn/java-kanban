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

                if ("/tasks/task/".equals(path)) {
                    sendResponse(exchange, 200, gson.toJson(manager.getTasks()));
                } else if ("/tasks/epic/".equals(path)) {
                    sendResponse(exchange, 200, gson.toJson(manager.getEpics()));
                } else if ("/tasks/subtask/".equals(path)) {
                    sendResponse(exchange, 200, gson.toJson(manager.getSubtasks()));
                } else {
                    sendResponse(exchange, 400, "Неправильный эндпоинт");
                }
            } else {
                sendResponse(exchange, 405, "");
            }
        }

        public void handleGetById(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String path = exchange.getRequestURI().getPath();
                String query = exchange.getRequestURI().getQuery();

                String[] pathSegments = path.split("/");
                if (pathSegments.length == 3 && query != null && query.startsWith("id=")) {
                    int id = Integer.parseInt(query.substring(3));
                    String entityType = pathSegments[2];
                    switch (entityType) {
                        case "task":
                            Task task = manager.getTask(id);
                            if (task != null) {
                                sendResponse(exchange, 200, gson.toJson(task));
                            } else {
                                sendResponse(exchange, 400, "Задача с id " + id + " не найдена");
                            }
                            break;
                        case "epic":
                            Epic epic = manager.getEpic(id);
                            if (epic != null) {
                                sendResponse(exchange, 200, gson.toJson(epic));
                            } else {
                                sendResponse(exchange, 400, "Эпик с id " + id + " не найден");
                            }
                            break;
                        case "subtask":
                            Subtask subtask = manager.getSubtask(id);
                            if (subtask != null) {
                                sendResponse(exchange, 200, gson.toJson(subtask));
                            } else {
                                sendResponse(exchange, 400, "Сабтаска с id " + id + " не найдена");
                            }
                            break;
                        default:
                            sendResponse(exchange, 400, "Неправильный тип");
                    }
                } else {
                    sendResponse(exchange, 400, "Неправильный эндпоинт");
                }
            } else {
                sendResponse(exchange, 405, "");
            }
        }

        public void handleGetSubtasksOfEpic(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String path = exchange.getRequestURI().getPath();
                String query = exchange.getRequestURI().getQuery();

                String[] pathSegments = path.split("/");
                if (pathSegments.length == 4 && query != null && query.startsWith("id=")) {
                    int epicId = Integer.parseInt(query.substring(3));
                    String entityType = pathSegments[3];

                    if ("epic".equals(entityType)) {
                        List<Subtask> subtasksOfEpic = manager.getSubtasksOfEpic(epicId);
                        Gson gson = new Gson();
                        sendResponse(exchange, 200, gson.toJson(subtasksOfEpic));
                    } else {
                        sendResponse(exchange, 400, "Неправильный эндпоинт");
                    }

                } else {
                    sendResponse(exchange, 405, "");
                }
            }
        }

        public void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String path = exchange.getRequestURI().getPath();

                if ("/tasks/".equals(path)) {
                    List<Task> prioritizedTasks = manager.getPrioritizedTasks();
                    sendResponse(exchange, 200, gson.toJson(prioritizedTasks));
                } else {
                    sendResponse(exchange, 400, "Неправильный эндпоинт");
                }

            } else {
                sendResponse(exchange, 405, "");
            }
        }

        public void handleGetHistory(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String path = exchange.getRequestURI().getPath();

                if ("/tasks/history/".equals(path)) {
                    List<Task> history = manager.getHistory();
                    sendResponse(exchange, 200, gson.toJson(history));
                } else {
                    sendResponse(exchange, 400, "Неправильный эндпоинт");
                }
            } else {
                sendResponse(exchange, 405, "");
            }
        }

        public void handleDelete(HttpExchange exchange) throws IOException {
            if ("DELETE".equals(exchange.getRequestMethod())) {
                String path = exchange.getRequestURI().getPath();

                if ("/tasks/task/".equals(path)) {
                    manager.removeAllTasks();
                    sendResponse(exchange, 200, "Все задачи удалены");
                } else if ("/tasks/epic/".equals(path)) {
                    manager.removeAllEpics();
                    sendResponse(exchange, 200, "Все эпики удалены");
                } else if ("/tasks/subtask/".equals(path)) {
                    manager.removeAllSubtasks();
                    sendResponse(exchange, 200, "Все сабтаски удалены");
                } else {
                    sendResponse(exchange, 400, "Неправильный эндпоинт");
                }
            } else {
                sendResponse(exchange, 405, "");
            }
        }

        public void handleDeleteById(HttpExchange exchange) throws IOException {
            if ("DELETE".equals(exchange.getRequestMethod())) {
                String path = exchange.getRequestURI().getPath();
                String query = exchange.getRequestURI().getQuery();

                String[] pathSegments = path.split("/");
                if (pathSegments.length == 3 && query != null && query.startsWith("id=")) {
                    int id = Integer.parseInt(query.substring(3));
                    String entityType = pathSegments[2];
                    switch (entityType) {
                            case "task":
                                manager.deleteTask(id);
                                sendResponse(exchange, 200, "Задача с id " + id + " удалена");
                                break;
                            case "epic":
                                manager.deleteEpic(id);
                                sendResponse(exchange, 200, "Эпик с id " + id + " удален");
                                break;
                            case "subtask":
                                manager.deleteSubtask(id);
                                sendResponse(exchange, 200, "Сабтаска с id " + id + " удалена");
                                break;
                            default:
                                sendResponse(exchange, 400, "Неправильный тип");
                                break;
                    }

                } else {
                    sendResponse(exchange, 400, "Неправильный URL формат");
                }

            } else {
                sendResponse(exchange, 405, "");
            }
        }

        private void handlePost(HttpExchange exchange) throws IOException {
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
                        sendResponse(exchange, 200, "Задача создана");
                        break;
                    case "/tasks/epic/":
                        epic = gson.fromJson(reader, Epic.class);
                        manager.addNewEpic(epic);
                        sendResponse(exchange, 200, "Эпик создан");
                        break;
                    case "/tasks/subtask/":
                        subtask = gson.fromJson(reader, Subtask.class);
                        manager.addNewSubtask(subtask);
                        sendResponse(exchange, 200, "Сабтаска создана");
                       break;
                    default:
                        sendResponse(exchange, 400, "Неправильный запрос");
                        break;
                }
            } catch (JsonSyntaxException e) {
                sendResponse(exchange, 400, "Неправильный формат JSON");
            }
        }

        private void handleUpdate(HttpExchange exchange) throws IOException {
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
                        sendResponse(exchange, 200, "Задача обновлена");
                        break;
                    case "/tasks/epic":
                        epic = gson.fromJson(reader, Epic.class);
                        manager.updateEpic(epic);
                        sendResponse(exchange, 200, "Эпик обновлен");
                        break;
                    case "/tasks/subtask":
                        subtask = gson.fromJson(reader, Subtask.class);
                        manager.updateSubtask(subtask);
                        sendResponse(exchange, 200, "Сабтаска обновлена");
                        break;
                    default:
                        sendResponse(exchange, 400, "Неправильный запрос");
                        break;
                }
            } catch (JsonSyntaxException e) {
                sendResponse(exchange, 400, "Неправильный формат JSON");
            }
        }
        private void sendResponse(HttpExchange exchange, int responseCode, String response) throws IOException {
            exchange.sendResponseHeaders(responseCode, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}



