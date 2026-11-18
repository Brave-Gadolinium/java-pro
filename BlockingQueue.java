import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public class BlockingQueue<T> {
    private final Deque<T> queue = new ArrayDeque<>();
    private final int capacity;

    private final Object notFull = new Object();
    private final Object notEmpty = new Object();

    public BlockingQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
    }

    public void enqueue(T element) throws InterruptedException {
        Objects.requireNonNull(element, "Element must not be null");

        synchronized (notFull) {
            while (true) {
                synchronized (queue) {
                    if (queue.size() < capacity) {
                        queue.addLast(element);
                        break;
                    }
                }
                notFull.wait();
            }
        }

        synchronized (notEmpty) {
            notEmpty.notify();
        }
    }

    public T dequeue() throws InterruptedException {
        T element;

        synchronized (notEmpty) {
            while (true) {
                synchronized (queue) {
                    if (!queue.isEmpty()) {
                        element = queue.removeFirst();
                        break;
                    }
                }
                notEmpty.wait();
            }
        }

        synchronized (notFull) {
            notFull.notify();
        }

        return element;
    }

    public int size() {
        synchronized (queue) {
            return queue.size();
        }
    }
}