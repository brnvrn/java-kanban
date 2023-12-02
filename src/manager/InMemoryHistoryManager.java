package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history;
    private final HashMap<Integer, Node> nodeMap;


    public InMemoryHistoryManager() {
        history = new ArrayList<>();
        nodeMap = new HashMap<>();
    }

    public void add(Task task) {
        if (task != null) {
            // Проверяем, есть ли задача в списке
            if (nodeMap.containsKey(task.getId())) {
                Node node = nodeMap.get(task.getId());
                // Удаляем задачу из списка
                removeNode(node);
            }

            // Добавляем задачу в конец списка
            linkLast(task);

            // Обновляем значение узла в HashMap
            nodeMap.put(task.getId(), tail);

            // Проверяем размер списка и удаляем первую задачу, если необходимо
            if (history.size() > 10) {
                Task removedTask = history.remove(0);
                nodeMap.remove(removedTask.getId());
            }
        }
    }

    @Override
    public void remove(int id) {
        if (nodeMap.containsKey(id)) {
            Node node = nodeMap.get(id);
            removeNode(node);
            history.remove(node.task);
            nodeMap.remove(id);
        }
    }

    public List<Task> getHistory() {
        return new ArrayList<>(history);
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
    public ArrayList<Task> getTasks() {
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
