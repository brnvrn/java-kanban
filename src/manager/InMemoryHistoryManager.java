package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node> nodeMap;


    public InMemoryHistoryManager() {
        nodeMap = new HashMap<>();
    }

    public void add(Task task) {
        if (task != null) {
            Node node = nodeMap.get(task.getId());
            if (node != null) {
                removeNode(node);
            }
            linkLast(task);
            nodeMap.put(task.getId(), tail);
        }
    }

    @Override
    public void remove(int id) {
        Node node = nodeMap.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }

    @Override
    public void removeAllHistory() {
        nodeMap.clear();
        head = null;
        tail = null;
    }

    public List<Task> getHistory() {
        return new ArrayList<>(getTasks());
    }

    private Node head;
    private Node tail;

    // Метод для добавления задачи в конец списка
    public void linkLast(Task task) {
        Node newNode = new Node(task);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
    }

    // Метод для получения всех задач из списка
    private ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node current = head;
        while (current != null) {
            tasks.add(current.task);
            current = current.next;
        }
        return tasks;
    }

    public void removeNode(Node node) {
        if (node == null) {
            return;
        }

        if (node == head) {
            head = node.next;
        }

        if (node == tail) {
            tail = node.prev;
        }

        if (node.prev != null) {
            node.prev.next = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        }
    }

    private static class Node {
        private final Task task;
        private Node prev;
        private Node next;

        public Node(Task task) {
            this.task = task;
            this.prev = null;
            this.next = null;
        }
    }
}
