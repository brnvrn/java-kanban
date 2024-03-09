package manager;

import com.google.gson.*;
import server.KVTaskClient;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.nio.file.Path;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient kvTaskClient;
    private static final Gson gson = new Gson();

    public HttpTaskManager(String serverUrl) {
        super(Path.of("data.csv").toFile());
        this.kvTaskClient = new KVTaskClient(serverUrl);
    }

    public void saveToServer() {
        kvTaskClient.put("tasks", gson.toJson(tasks.values()));
        kvTaskClient.put("epics", gson.toJson(epics.values()));
        kvTaskClient.put("subtasks", gson.toJson(subtasks.values()));
        kvTaskClient.put("prioritizedTasks", gson.toJson(getPrioritizedTasks()));
        kvTaskClient.put("history", gson.toJson(getHistory()));
    }

    public void loadFromServer() {
        String tasksJson = kvTaskClient.load("tasks");
        String epicsJson = kvTaskClient.load("epics");
        String subtasksJson = kvTaskClient.load("subtasks");
        String prioritizedTasksJson = kvTaskClient.load("prioritizedTasks");
        String historyJson = kvTaskClient.load("history");

        JsonParser parser = new JsonParser();

        JsonArray tasksArray = parser.parse(tasksJson).getAsJsonArray();
        JsonArray epicsArray = parser.parse(epicsJson).getAsJsonArray();
        JsonArray subtasksArray = parser.parse(subtasksJson).getAsJsonArray();
        JsonArray prioritizedTasksArray = parser.parse(prioritizedTasksJson).getAsJsonArray();
        JsonArray historyArray = parser.parse(historyJson).getAsJsonArray();

        for (JsonElement element : tasksArray) {
            Task task = gson.fromJson(element, Task.class);
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
        }

        for (JsonElement element : epicsArray) {
            Epic epic = gson.fromJson(element, Epic.class);
            epics.put(epic.getId(), epic);
        }

        for (JsonElement element : subtasksArray) {
            Subtask subtask = gson.fromJson(element, Subtask.class);
            subtasks.put(subtask.getId(), subtask);
            prioritizedTasks.add(subtask);
        }

        for (JsonElement element : historyArray) {
            JsonObject obj = element.getAsJsonObject();
            int id = obj.get("id").getAsInt();
            if (tasks.containsKey(id)) {
                historyManager.add(tasks.get(id));
            } else if (epics.containsKey(id)) {
                historyManager.add(epics.get(id));
            } else if (subtasks.containsKey(id)) {
                historyManager.add(subtasks.get(id));
            }
        }
        for (JsonElement element : prioritizedTasksArray) {
            if (element.isJsonObject()) {
                JsonObject obj = element.getAsJsonObject();
                if (obj.has("type")) {
                    String type = obj.get("type").getAsString();
                    if ("task".equals(type)) {
                        Task task = gson.fromJson(obj, Task.class);
                        prioritizedTasks.add(task);
                    } else if ("subtask".equals(type)) {
                        Subtask subtask = gson.fromJson(obj, Subtask.class);
                        prioritizedTasks.add(subtask);
                    }
                }
            }
        }
    }
}
