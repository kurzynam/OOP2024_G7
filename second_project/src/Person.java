import com.sun.deploy.util.StringUtils;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Person implements Serializable{
    private String name;
    private LocalDate birthDate;
    private LocalDate deathDate;

    private Person mother;
    private Person father;

    public Person(String fullName, LocalDate birthDate, LocalDate deathDate) {
        this.name = fullName;
        this.birthDate = birthDate;
        this.deathDate = deathDate;
    }
    public Person(String fullName) {
        this.name = fullName;
    }

    public Person(String fullName, LocalDate birthDate, LocalDate deathDate, Person mother, Person father) {
        this.name = fullName;
        this.birthDate = birthDate;
        this.deathDate = deathDate;
        this.mother = mother;
        this.father = father;
    }


    public  String generateTree() {
        String result = "@startuml\n%s\n%s@enduml";
        Function<Person, String> objectName = person -> person.getName().replaceAll(" ", "");
        Function<Person, String> objectLine = person -> String.format("object \"%s\" as %s", person.getName(), objectName.apply(person));
        StringBuilder objects = new StringBuilder();
        StringBuilder relations = new StringBuilder();


        objects.append(objectLine.apply(this)).append("\n");
        if (father != null){
            objects.append(objectLine.apply(father)).append("\n");
            relations.append(String.format("%s <-- %s\n", objectName.apply(father), objectName.apply(this)));
        }
        if (mother != null){
            objects.append(objectLine.apply(mother)).append("\n");
            relations.append(String.format("%s <-- %s\n", objectName.apply(mother), objectName.apply(this)));
        }


        return String.format(result, objects, relations);
    }




    public static Person getOldestLivingPerson(List<Person> people){
        return people.stream()
                .filter(p -> p.isAlive())
                .min(Comparator.comparing(p -> p.getBirthDate()))
                .orElse(null);
    }

    public static String generateDiagram(List<Person> people) {
        String fileContent = "@startuml\n%s\n@enduml";
        String fileBody = "";
        Function<String, String> objectName = str -> str.replace(" ", "");
        Function<String, String> objectLine = str -> String.format("object \"%s\" as %s\n", str, objectName.apply(str));
        List<String> convertedPeople = people.stream().map(person -> person.name).map(objectLine).collect(Collectors.toList());
        for(String str : convertedPeople) {
            fileBody += str;
        }

        Function<Person, String> fatherRelation = person -> String.format("%s <-- %s\n", objectName.apply(person.name), objectName.apply(person.father.name));
        Function<Person, String> motherRelation = person -> String.format("%s <-- %s\n", objectName.apply(person.name), objectName.apply(person.mother.name));
        Predicate<Person> hasFather = person -> person.father != null;
        Predicate<Person> hasMother = person -> person.mother != null;

        List<String> relationsList = people.stream().filter(hasFather).map(fatherRelation).collect(Collectors.toList());
        for(String str : relationsList) {
            fileBody += str;
        }

        relationsList = people.stream().filter(hasMother).map(motherRelation).collect(Collectors.toList());
        for(String str : relationsList) {
            fileBody += str;
        }

        return String.format(fileContent, fileBody);
    }
//    public static String generateDiagram(List<Person> people, Predicate<Person> condition, Function<String, String> postProcess) {
//        String result = "@startuml\n%s\n%s\n@enduml";
//        Function<String, String> objectName = str -> str.replaceAll("\\s+", "");
//        Function<String, String> objectLine = str -> String.format("object \"%s\" as %s",str, objectName.apply(str));
//        Function<String, String> objectLineAndPostprocess = objectLine.andThen(postProcess);
//
//        Map<Boolean, List<Person>> groupedPeople = people.stream()
//                .collect(Collectors.partitioningBy(condition));
//
//        Set<String> objects = groupedPeople.get(true).stream()
//                .map(person -> person.getFullName())
//                .map(objectLineAndPostprocess)
//                .collect(Collectors.toSet());
//        objects.addAll(groupedPeople.get(false).stream()
//                .map(person -> person.getFullName())
//                .map(objectLine)
//                .collect(Collectors.toSet())
//        );
//
//        Set<String> fathers = people.stream()
//                .filter(p -> p.getFather() != null)
//                .map(p -> String.format("%s <-- %s", p.getFullName(), p.getFather().getFullName()))
//                .collect(Collectors.toSet());
//
//        Set<String> mothers = people.stream()
//                .filter(p -> p.getMother() != null)
//                .map(p -> String.format("%s <-- %s", p.getFullName(), p.getMother().getFullName()))
//                .collect(Collectors.toSet());
//
//        String objectString = String.join("\n", objects);
//        String relationString = String.join("\n", fathers)
//                + String.join("\n", mothers) ;
//
//        return String.format(result, objectString, relationString);
//    }




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
                if (!motherFullName.isEmpty()) {
                    mother = find(motherFullName, people);
                    if (mother == null) {
                        mother = new Person(motherFullName);
                    }
                    mother = verifyParent(mother, birthDate, motherFullName, fullName);
                }
            }
            if (numOfColumns > 4){
                String fatherFullName = parts[4].trim();
                if (!fatherFullName.isEmpty()){
                    father = find(fatherFullName, people);
                    if (father == null){
                        father = new Person(fatherFullName);
                    }
                    father = verifyParent(father, birthDate, fatherFullName, fullName);
                }

            }
            if (deathDate != null && deathDate.isBefore(birthDate))
                throw new NegativeLifespanException(fullName);
            Person personToAdd = find(fullName, people);
            if (isPersonOnTheList(personToAdd, people))
                throw new AmbigiousPersonException(personToAdd.getName());
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


    public int getLifeLength(){
        if (deathDate == null){
            return LocalDate.now().getYear() - birthDate.getYear();
        }
        return deathDate.getYear() - birthDate.getYear();
    }
    public static List<Person> getPeopleMatchingPattern(List<Person> people, String pattern){
        return people.stream().filter(person -> person.name.contains(pattern)).collect(Collectors.toList());
    }

    public static List<Person> getDeadPeople(List<Person> people){
        return people.stream().filter(p -> p.deathDate != null).sorted(Comparator.comparing(person -> person.getLifeLength())).collect(Collectors.toList());
    }




    private static Person verifyParent(Person parent, LocalDate birthDate, String parentFullName, String fullName) throws IOException {
        try {
            if((!parent.isAlive() && parent.getDeathDate().isBefore(birthDate)) || parent.getAge() < 15) {
                throw new ParentingAgeException(parent.getName());
            }
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

    public String getName() {
        return name;
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
        if (birthDate == null){
            return -1;
        }
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
        sb.append("Imię i nazwisko: " + getName() + "\n");

        if(getBirthDate() != null){
            sb.append("Data urodzenia: " + getBirthDate() + "\n");
            sb.append("Wiek: " + getAge() + "\n" );
        }
        if(getDeathDate() != null) {
            sb.append("Data śmierci: " + getDeathDate() + "\n");
        }
        sb.append("Czy żyje: " + (isAlive() ? "tak" : "nie") + "\n");
        return sb.toString();
    }

    public boolean equals(Person other){
        if (other == null || (getName() != null && other.getName() == null))
            return false;
        else if(getName() == null && other.getName() != null)
            return false;
        return getName().equals(other.getName());
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
            if(fullname.equals(p.getName()))
                return p;
            if(p.getMother() != null && fullname.equals(p.getMother().getName()))
                return p.getMother();
            if(p.getFather() != null && fullname.equals(p.getFather().getName()))
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