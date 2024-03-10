package manager;

import com.google.gson.*;
import server.KVTaskClient;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient kvTaskClient;
    private static final Gson gson = new Gson();

    public HttpTaskManager(String serverUrl) {
        super(Path.of("data.csv").toFile());
        this.kvTaskClient = new KVTaskClient(serverUrl);
    }

    @Override
    public void save() {
        kvTaskClient.put("tasks", gson.toJson(tasks.values()));
        kvTaskClient.put("epics", gson.toJson(epics.values()));
        kvTaskClient.put("subtasks", gson.toJson(subtasks.values()));

        List<Integer> historyIds = new ArrayList<>();
        for (Task task : getHistory()) {
            historyIds.add(task.getId());
        }
        kvTaskClient.put("history", gson.toJson(historyIds));
    }

    public void loadFromServer() {

        String tasksJson = kvTaskClient.load("tasks");
        String epicsJson = kvTaskClient.load("epics");
        String subtasksJson = kvTaskClient.load("subtasks");
        String historyJson = kvTaskClient.load("history");

        JsonParser parser = new JsonParser();

        JsonArray tasksArray = parser.parse(tasksJson).getAsJsonArray();
        JsonArray epicsArray = parser.parse(epicsJson).getAsJsonArray();
        JsonArray subtasksArray = parser.parse(subtasksJson).getAsJsonArray();
        JsonArray historyArray = parser.parse(historyJson).getAsJsonArray();

        for (JsonElement element : tasksArray) {
            Task task = gson.fromJson(element, Task.class);
            tasks.put(task.getId(), task);
            if (task.getId() > generatorId) {
                generatorId = task.getId();
            }
            prioritizedTasks.add(task);
        }

        for (JsonElement element : epicsArray) {
            Epic epic = gson.fromJson(element, Epic.class);
            epics.put(epic.getId(), epic);
            if (epic.getId() > generatorId) {
                generatorId = epic.getId();
            }
        }

        for (JsonElement element : subtasksArray) {
            Subtask subtask = gson.fromJson(element, Subtask.class);
            subtasks.put(subtask.getId(), subtask);
            if (subtask.getId() > generatorId) {
                generatorId = subtask.getId();
            }
            prioritizedTasks.add(subtask);
        }

        for (JsonElement element : historyArray) {
            int id = element.getAsInt();
            if (tasks.containsKey(id)) {
                historyManager.add(tasks.get(id));
            } else if (epics.containsKey(id)) {
                historyManager.add(epics.get(id));
            } else if (subtasks.containsKey(id)) {
                historyManager.add(subtasks.get(id));
            }
        }
    }
}
