package tasks;


public class Subtask extends Task {
    protected int epicId;

    public Subtask(int id, TaskType type, String name, TaskStatus status, String description, int epicId) {
        super(id, type, name, status, description);
        this.epicId = epicId;
    }

    public Subtask(TaskType type, String name, TaskStatus status, String description, int epicId) {
        super(type, name, status, description);
        this.epicId = epicId;
    }

    public Subtask(int id, TaskType type, String name, TaskStatus status, String description) {
        super(id, type, name, status, description);
    }

    @Override
    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "tasks.Subtask{" +
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
