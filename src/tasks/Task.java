package tasks;

import java.util.Objects;

    public class Task {

        protected int id;
        protected String name;
        protected TaskStatus status;
        protected String description;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public TaskStatus getStatus() {
            return status;
        }

        public void setStatus(TaskStatus status) {
            this.status = status;
        }

        public Task(int id, String name, String description, TaskStatus status) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.status = status;
        }

        public Task(String name, String description, TaskStatus status) {
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
        public int hashCode() {
            return Objects.hash(id, name, status, description);
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
