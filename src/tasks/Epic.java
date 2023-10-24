package tasks;

import java.util.ArrayList;

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
