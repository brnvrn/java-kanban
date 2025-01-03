package tasks;

import java.time.LocalDateTime;
import java.util.Objects;

    public class Task {

        protected int id;
        protected String name;
        protected TaskStatus status;
        protected String description;
        protected TaskType type;
        protected int duration;
        protected LocalDateTime startTime;

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

        public Task(int id, TaskType type, String name, TaskStatus status, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.status = status;
            this.type = type;
        }
        public Task(int id, TaskType type, String name, TaskStatus status, String description, int duration, LocalDateTime startTime) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.status = status;
            this.type = type;
            this.duration = duration;
            this.startTime = startTime;
        }


        public Task(TaskType type, String name, TaskStatus status, String description) {
            this.name = name;
            this.description = description;
            this.status = status;
            this.type = type;
        }
        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public LocalDateTime getStartTime() {
            return startTime;
        }

        public void setStartTime(LocalDateTime startTime) {
            this.startTime =  startTime;
        }
        public LocalDateTime getEndTime() {
            if (startTime != null && duration != 0) {
                return startTime.plusMinutes(duration);
            }
            return null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)  return true;
            if (o == null || getClass() != o.getClass()) return false;
            tasks.Task task = (tasks.Task) o;
            return id == task.id && Objects.equals(name, task.name) && Objects.equals(status, task.status) &&
                    Objects.equals(description, task.description)&& duration == task.duration &&
                    Objects.equals(startTime, task.startTime);
        }
        @Override
        public int hashCode() {
            return Objects.hash(id, name, status, description, duration, startTime);
        }

        @Override
        public String toString() {
            return "tasks.Task{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", status='" + status + '\'' +
                    ", description='" + description + '\'' +
                    ", duration=" + duration +
                    ", startTime=" + startTime +
                    '}';
        }
    }
