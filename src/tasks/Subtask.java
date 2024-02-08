package tasks;


import java.time.LocalDateTime;

public class Subtask extends Task {

    private int epicId;

    public Subtask(int id, TaskType type, String name, TaskStatus status, String description, int epicId) {
        super(id, type, name, status, description);
        this.epicId = epicId;
    }

    public Subtask(TaskType type, String name, TaskStatus status, String description, int epicId) {
        super(type, name, status, description);
        this.epicId = epicId;
    }
    public Subtask(int id, TaskType type, String name, TaskStatus status, String description, int duration, LocalDateTime startTime, int epicId) {
        super(id, type, name, status, description, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(int id, TaskType type, String name, TaskStatus status, String description) {
        super(id, type, name, status, description);
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", duration=" + duration +
                ", startTime=" + startTime +
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
        Subtask subtask = (Subtask) obj;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        int code = super.hashCode();
        code = 31 * code + epicId;
        return code;
    }
}
