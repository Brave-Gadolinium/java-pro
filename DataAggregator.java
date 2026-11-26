import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

final class ProductInfo {
    private final String name;
    private final double price;
    private final String description;
    private final double rating;

    ProductInfo(String name, double price, String description, double rating) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "ProductInfo{name='" + name
                + "', price=" + price
                + ", description='" + description
                + "', rating=" + rating + "}";
    }
}

public class DataAggregator {
    private final ExecutorService executor;

    public DataAggregator(ExecutorService executor) {
        this.executor = executor;
    }

    public ProductInfo aggregateProductInfo(String productName) {
        return aggregateProductInfoAsync(productName).join();
    }

    public CompletableFuture<ProductInfo> aggregateProductInfoAsync(String productName) {
        CompletableFuture<Double> price = CompletableFuture
                .supplyAsync(() -> fetchPrice(productName), executor)
                .exceptionally(error -> 0.0);

        CompletableFuture<String> description = CompletableFuture
                .supplyAsync(() -> fetchDescription(productName), executor)
                .exceptionally(error -> "Нет данных");

        CompletableFuture<Double> rating = CompletableFuture
                .supplyAsync(() -> fetchRating(productName), executor)
                .exceptionally(error -> 0.0);

        return CompletableFuture.allOf(price, description, rating)
                .thenApply(ignored -> new ProductInfo(
                        productName, price.join(), description.join(), rating.join()));
    }

    private double fetchPrice(String productName) {
        simulateService("Цена", productName);
        return 899.99;
    }

    private String fetchDescription(String productName) {
        simulateService("Описание", productName);
        return "Ноутбук для работы и учёбы";
    }

    private double fetchRating(String productName) {
        simulateService("Рейтинг", productName);
        return 4.7;
    }

    private static void simulateService(String service, String productName) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        try {
            Thread.sleep(random.nextLong(1000, 3001));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Запрос прерван: " + service, e);
        }

        if (random.nextDouble() < 0.20) {
            throw new RuntimeException(service + ": сбой для товара " + productName);
        }
    }

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        DataAggregator aggregator = new DataAggregator(executor);

        aggregator.aggregateProductInfoAsync("Ноутбук")
                .thenAccept(System.out::println)
                .whenComplete((ignored, error) -> executor.shutdown());

    }
}