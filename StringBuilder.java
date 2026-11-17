import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public final class StringBuilder {
    private static final int DEFAULT_CAPACITY = 16;

    private char[] buffer = new char[DEFAULT_CAPACITY];
    private int length;

    private final Deque<char[]> history = new ArrayDeque<>();

    public StringBuilder append(String value) {
        value = String.valueOf(value);

        if (value.isEmpty()) {
            return this;
        }

        int newLength = Math.addExact(length, value.length());
        ensureCapacity(newLength);
        saveSnapshot();

        value.getChars(0, value.length(), buffer, length);
        length = newLength;
        return this;
    }

    public StringBuilder insert(int index, String value) {
        checkPosition(index);
        value = String.valueOf(value);

        if (value.isEmpty()) {
            return this;
        }

        int valueLength = value.length();
        int newLength = Math.addExact(length, valueLength);
        ensureCapacity(newLength);
        saveSnapshot();

        System.arraycopy(buffer, index, buffer, index + valueLength, length - index);
        value.getChars(0, valueLength, buffer, index);
        length = newLength;
        return this;
    }

    public StringBuilder delete(int start, int end) {
        end = Math.min(end, length);
        if (start < 0 || start > end) {
            throw new StringIndexOutOfBoundsException();
        }

        if (start == end) {
            return this;
        }

        saveSnapshot();

        System.arraycopy(buffer, end, buffer, start, length - end);
        length -= end - start;
        return this;
    }

    public StringBuilder undo() {
        if (!history.isEmpty()) {
            buffer = history.pop();
            length = buffer.length;
        }
        return this;
    }

    public int length() {
        return length;
    }

    @Override
    public String toString() {
        return new String(buffer, 0, length);
    }

    private void saveSnapshot() {
        history.push(Arrays.copyOf(buffer, length));
    }

    private void checkPosition(int index) {
        if (index < 0 || index > length) {
            throw new StringIndexOutOfBoundsException(index);
        }
    }

    private void ensureCapacity(int minimumCapacity) {
        if (minimumCapacity <= buffer.length) {
            return;
        }

        long grown = (long) buffer.length * 2 + 2;
        int newCapacity = (int) Math.min(
                Integer.MAX_VALUE,
                Math.max(grown, (long) minimumCapacity)
        );
        buffer = Arrays.copyOf(buffer, newCapacity);
    }
}