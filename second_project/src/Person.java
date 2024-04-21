import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Person implements Serializable{
    private String fullName;
    private LocalDate birthDate;
    private LocalDate deathDate;

    private Person mother;
    private Person father;

    public Person(String fullName, LocalDate birthDate, LocalDate deathDate) {
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.deathDate = deathDate;
    }
    public Person(String fullName) {
        this.fullName = fullName;
    }

    public Person(String fullName, LocalDate birthDate, LocalDate deathDate, Person mother, Person father) {
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.deathDate = deathDate;
        this.mother = mother;
        this.father = father;
    }



    public int getLifeLength(){
        if (isAlive())
            return LocalDate.now().getYear() - getBirthDate().getYear();
        return getDeathDate().getYear() - getBirthDate().getYear();
    }

    public static List<Person> getDeadPeople(List<Person> people){
        return people.stream()
                .filter(person -> !person.isAlive())
                .sorted(Comparator.comparing(person -> person.getLifeLength()))
                .collect(Collectors.toList());
    }

    public static Person getOldestLivingPerson(List<Person> people){
        return people.stream()
                .filter(p -> p.isAlive())
                .min(Comparator.comparing(p -> p.getBirthDate()))
                .orElse(null);
    }
    public static String generateDiagram(List<Person> people, Predicate<Person> condition, Function<String, String> postProcess) {
        String result = "@startuml\n%s\n%s\n@enduml";
        Function<String, String> objectName = str -> str.replaceAll("\\s+", "");
        Function<String, String> objectLine = str -> String.format("object \"%s\" as %s",str, objectName.apply(str));
        Function<String, String> objectLineAndPostprocess = objectLine.andThen(postProcess);

        Map<Boolean, List<Person>> groupedPeople = people.stream()
                .collect(Collectors.partitioningBy(condition));

        Set<String> objects = groupedPeople.get(true).stream()
                .map(person -> person.getFullName())
                .map(objectLineAndPostprocess)
                .collect(Collectors.toSet());
        objects.addAll(groupedPeople.get(false).stream()
                .map(person -> person.getFullName())
                .map(objectLine)
                .collect(Collectors.toSet())
        );

        Set<String> fathers = people.stream()
                .filter(p -> p.getFather() != null)
                .map(p -> String.format("%s <-- %s", p.getFullName(), p.getFather().getFullName()))
                .collect(Collectors.toSet());

        Set<String> mothers = people.stream()
                .filter(p -> p.getMother() != null)
                .map(p -> String.format("%s <-- %s", p.getFullName(), p.getMother().getFullName()))
                .collect(Collectors.toSet());

        String objectString = String.join("\n", objects);
        String relationString = String.join("\n", fathers)
                + String.join("\n", mothers) ;

        return String.format(result, objectString, relationString);
    }




    public static List<Person> fromCsvFile(String filePath) {
        List<Person> people = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                fromCsvLine(line, people);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return people;
    }
    public static void fromCsvLine(String csvLine, List<Person> people) {
        try {
            String[] parts = csvLine.split(",");
            LocalDate birthDate = null;
            LocalDate deathDate = null;
            Person mother = null;
            Person father = null;
            String fullName = null;
            int numOfColumns = parts.length;
            if (numOfColumns > 0){
                fullName = parts[0].trim();
            }
            if (numOfColumns > 1){
                birthDate = LocalDate.parse(parts[1].trim(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            }

            if (numOfColumns > 2 && !parts[2].trim().isEmpty()) {
                deathDate = LocalDate.parse(parts[2].trim(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            }
            if (numOfColumns > 3) {
                String motherFullName = parts[3].trim();
                mother = find(motherFullName, people);
                if (mother == null && !motherFullName.isEmpty()){
                    mother = new Person(fullName);
                }
                mother = verifyParent(mother, birthDate, motherFullName, fullName);
            }
            if (numOfColumns > 4){
                String fatherFullName = parts[4].trim();
                father = find(fatherFullName, people);
                if (father == null && !fatherFullName.isEmpty()){
                    father = new Person(fullName);
                }
                father = verifyParent(father, birthDate, fatherFullName, fullName);
            }
            if (deathDate != null && deathDate.isBefore(birthDate))
                throw new NegativeLifespanException(fullName);
            Person personToAdd = find(fullName, people);
            if (isPersonOnTheList(personToAdd, people))
                throw new AmbigiousPersonException(personToAdd.getFullName());
            if (personToAdd == null){
                personToAdd = new Person(fullName, birthDate, deathDate, mother, father);
            }else {
                personToAdd.birthDate = birthDate;
                personToAdd.deathDate = deathDate;
                personToAdd.mother = mother;
                personToAdd.father = father;
            }
            people.add(personToAdd);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static Person verifyParent(Person parent, LocalDate birthDate, String parentFullName, String fullName) throws IOException {
        try {
            if(parent.getDeathDate().isBefore(birthDate) || parent.getAge() < 15)
                throw  new ParentingAgeException(parent.getFullName());
        }catch (ParentingAgeException e){
            System.out.println(e.getMessage());
            System.out.println(String.format("Czy potwierdzasz, że %s jest rodzicem %s (T/N)?", parentFullName, fullName));
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            if (!"T".equalsIgnoreCase(reader.readLine())){
                return null;
            }
        }
        return parent;
    }

    public String getFullName() {
        return fullName;
    }
    public LocalDate getBirthDate() {
        return birthDate;
    }
    public LocalDate getDeathDate() {
        return deathDate;
    }
    public boolean isAlive() {
        return deathDate == null;
    }

    public Person getMother() {
        return mother;
    }

    public Person getFather() {
        return father;
    }

    public int getAge() {
        if (isAlive()){
            LocalDate currentDate = LocalDate.now();
            return currentDate.minusYears(birthDate.getYear()).getYear();
        }else {
            return getDeathDate().minusYears(birthDate.getYear()).getYear();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Imię i nazwisko: " + getFullName() + "\n")
                .append("Data urodzenia: " + getBirthDate() + "\n");
        if(getDeathDate() != null)
            sb.append("Data śmierci: " + getDeathDate() + "\n");
        sb.append("Wiek: " + getAge() + "\n" )
                .append("Czy żyje: " + (isAlive() ? "tak" : "nie") + "\n");
        return sb.toString();
    }

    public boolean equals(Person other){
        if (other == null || (getFullName() != null && other.getFullName() == null))
            return false;
        else if(getFullName() == null && other.getFullName() != null)
            return false;
        return getFullName().equals(other.getFullName());
    }
    public static boolean isPersonOnTheList(Person person, List<Person> people){
        if (person == null)
            return false;
        for(Person p : people){
            if (person.equals(p))
                return true;
        }
        return false;
    }

    public static Person find(String fullname, List<Person> people){
        if (fullname == null || fullname.isEmpty())
            return null;
        for(Person p : people){
            if(fullname.equals(p.getFullName()))
                return p;
            if(p.getMother() != null && fullname.equals(p.getMother().getFullName()))
                return p.getMother();
            if(p.getFather() != null && fullname.equals(p.getFather().getFullName()))
                return p.getFather();
        }
        return null;
    }

    public static void toBinaryFile(List<Person> people, String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(people);
            System.out.println("Zapisano dane do pliku binarnego: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static List<Person> fromBinaryFile(String filePath) {
        List<Person> people = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                List<?> list = (List<?>) obj;
                for (Object item : list) {
                    if (item instanceof Person) {
                        people.add((Person) item);
                    }
                }
            }
            System.out.println("Wczytano dane z pliku binarnego: " + filePath);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return people;
    }
}
