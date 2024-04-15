public class Main {
    public static void main(String[] args) {
        CustomList<String> newList = new CustomList<>();
        newList.addLast("Hello");
        System.out.println(newList.getLast());
        newList.addLast("World");
        System.out.println(newList.getLast());



    }

}