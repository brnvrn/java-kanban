package tasks;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
        private final ArrayList<Integer> subtaskIds;

        public Epic(int id, TaskType type, String name, TaskStatus status, String description) {
            super(id, type, name, status, description);
            subtaskIds = new ArrayList<>();
        }

        public Epic(TaskType type, String name, TaskStatus status, String description) {
            super(type, name, status, description);
            subtaskIds = new ArrayList<>();
        }

    public void addSubtaskId (int id) {
            subtaskIds.add(id);
        }
        public ArrayList<Integer> getSubtaskIds() {
            return subtaskIds;
        }
        public void cleanSubtaskId() {
            subtaskIds.clear();
        }

    @Override
        public String toString() {
            return " tasks.Epic{" +
                    "№=" + id +
                    ", Name='" + name + '\'' +
                    ", Description='" + description + '\'' +
                    ", Status='" + status + '\'' +
                    '}';
        }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        Epic epic = (Epic) obj;
        return Objects.equals(subtaskIds, epic.subtaskIds);
    }
    @Override
    public int hashCode() {
        int code = super.hashCode();
        code = 31 * code + Objects.hashCode(subtaskIds);
        return code;
    }
    }
