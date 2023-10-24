package tasks;

import java.util.Objects;
    public class Task {

        protected int id;
        protected String name;
        protected String status;
        protected String description;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Task(int id, String name, String description, String status) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.status = status;
        }

        public Task(String name, String description, String status) {
            this.name = name;
            this.description = description;
            this.status = status;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)  return true;
            if (o == null || getClass() != o.getClass()) return false;
            tasks.Task task = (tasks.Task) o;
            return id == task.id && Objects.equals(name, task.name) && Objects.equals(status, task.status) &&
                    Objects.equals(description, task.description);
        }

        @Override
        public String toString() {
            return "tasks.Task{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", status='" + status + '\'' +
                    ", description='" + description + '\'' +
                    '}';
        }
    }
