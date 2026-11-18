public class Main {
    public static void main(String[] args) {
        StringBuilder builder = new StringBuilder();

        builder.append("Hello")
                .append(" world")
                .delete(5, 11);

        System.out.println(builder);

        builder.undo();
        System.out.println(builder);

        builder.undo();
        System.out.println(builder);
    }
}