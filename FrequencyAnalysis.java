import java.util.HashMap;
import java.util.Map;

public class FrequencyAnalysis {
    public static <T> Map<T, Integer> count(T[] elements) {
        Map<T, Integer> frequencies = new HashMap<>();

        for (T element : elements) {
            frequencies.merge(element, 1, Integer::sum);
        }

        return frequencies;
    }
}