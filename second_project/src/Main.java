import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class Main {
    public static void main(String[] args) {
        CusomList<String> newList = new CusomList<>();
        newList.addLast("Hello");
        System.out.println(newList.getLast());
        newList.addLast("World");
        System.out.println(newList.getLast());



    }

}