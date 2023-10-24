package tasks;

public class Subtask extends Task {
    protected int epicId;

    public Subtask(int id, String name, String description, String status, int epicId) {
        super(id, name, status, description);
        this.epicId = epicId;
    }

    public Subtask(String name,  String description, String status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }
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
}
