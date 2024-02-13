package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds;
    private LocalDateTime endTime;

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    @Override
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

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
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", subtaskIds=" + subtaskIds +
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
        return Objects.equals(subtaskIds, epic.subtaskIds) &&
                Objects.equals(endTime, epic.endTime);
    }
    @Override
    public int hashCode() {
        int code = super.hashCode();
        code = 31 * code + Objects.hashCode(subtaskIds);
        code = 31 * code + Objects.hashCode(endTime);
        return code;
    }
    }
