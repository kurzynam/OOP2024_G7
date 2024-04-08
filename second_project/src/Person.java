import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Person {

    private String name;
    private LocalDate dateOfBirth;
    private LocalDate dateOfDeath;
    private Person mother;
    Person father;

    public Person(String name, LocalDate dateOfBirth, LocalDate dateOfDeath, Person mother, Person father) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.dateOfDeath = dateOfDeath;
        this.mother = mother;
        this.father = father;
    }

    public static List<Person> fromCsv(String path){
        List<Person> people = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            br.readLine();
            String line;
            while ((line = br.readLine()) != null){
                fromCsvLine(line, people);
            }
            return people;
        } catch (Exception e) {
            System.err.println("Nie mogę odczytać pliku");
            return null;
        }
    }



    public static void fromCsvLine(String line, List<Person> people){

        try{
            String[] parts = line.split(",");
            String name = null;
            LocalDate dateOfBirth = null;
            LocalDate dateOfDeath = null;
            Person mother = null;
            Person father = null;
            int numOfCols = parts.length;
            if (numOfCols > 0){
                name = parts[0].trim();
            }

            if(numOfCols > 1){
                String dob = parts[1].trim();
                if (!dob.isEmpty()){
                    dateOfBirth = LocalDate.parse(dob, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                }
            }
            if(numOfCols > 2){
                String dod = parts[2].trim();
                if (!dod.isEmpty()){
                    dateOfDeath = LocalDate.parse(dod, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                }
            }
            if (dateOfBirth != null && dateOfDeath != null && dateOfDeath.isBefore(dateOfBirth))
                throw new NegativeLifespanException(name);
            people.add(new Person(name, dateOfBirth, dateOfDeath, mother, father));
        }
        catch (Exception e){
            System.err.println(e.getMessage());
        }

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Person{" );
        sb.append("name='" + name + '\'' );
        if (dateOfBirth != null)
            sb.append(", date of birth=" + dateOfBirth);
        if(dateOfDeath != null)
            sb.append(", date of death=" + dateOfDeath);
        if(mother != null)
            sb.append(", mother=" + mother.name);
        if(father != null)
            sb.append(", father=" + father.name );
        sb.append('}');
        return sb.toString();
    }

    public static String generateDiagram(List<Person> people){
        String fileContent = "@startuml \n %s \n @enduml";
        String fileBody = "";
        Function<String, String> objectName = str -> str.replace(" ", "");
        Function<String, String> objectLine = str -> String.format("object \"%s\" as %s", str, objectName.apply(str));
        List<String> convertedPeople = people.stream().map(person -> person.name).map(objectLine).collect(Collectors.toList());
        for (String p : convertedPeople ){
            fileBody += p + "\n";
        }


        return String.format(fileContent,fileBody);
    }
}
