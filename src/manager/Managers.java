package manager;

import java.io.File;

public class Managers {
    public static TaskManager getDefault(File saveFile) {

        return new FileBackedTasksManager(saveFile);
    }
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
