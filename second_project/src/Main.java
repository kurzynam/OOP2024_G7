import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class Main {
    public static void main(String[] args) {
        List<Person> people = Person.fromCsvFile("family.csv");
        String output = Person.generateDiagram(people);
        System.out.println(output);


    }

    public static boolean startsWithB(String toCheck){
        return toCheck.startsWith("B");
    }
}