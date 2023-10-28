package tasks;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
        protected ArrayList<Integer> subtaskIds;

        public Epic(int id, String name, String description, String status) {
            super(id, name, status, description);
        }

        public Epic(String name,  String description, String status) {
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
    }
