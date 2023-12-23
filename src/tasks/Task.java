package tasks;

import java.util.Objects;

    public class Task {

        protected int id;
        protected String name;
        protected TaskStatus status;
        protected String description;
        protected TaskType type;

        public void setEpicId(int epicId) {
            this.epicId = epicId;
        }

        protected int epicId;

        public int getEpicId() {
            return epicId;
        }

        public Task() {
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public void setType(TaskType type) {
            this.type = type;
        }

        public TaskType getType() {
            return type;
        }


        public TaskStatus getStatus() {
            return status;
        }

        public String getName() {
            return name;
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

        public Task(TaskType type, String name, TaskStatus status, String description) {
            this.name = name;
            this.description = description;
            this.status = status;
            this.type = type;
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

        public void setEpic(String value) {
        }
    }
