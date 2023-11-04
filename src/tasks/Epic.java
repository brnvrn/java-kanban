package tasks;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
        protected ArrayList<Integer> subtaskIds;

        public Epic(int id, String name, String description, TaskStatus status) {
            super(id, name, description, status);
        }

        public Epic(String name,  String description, TaskStatus status) {
            super(name, description, status);
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

        public String getName() {
            return name;
        }
        public String getDescription() {
            return description;
        }
        public void setName(String name) {
            this.name = name;
        }
        public void setDescription(String description) {
            this.description = description;
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
