public class ParentingAgeException extends Exception{
    public ParentingAgeException(String fullname) {
        super(String.format("Wiek rodzica %s budzi wątpliwości!", fullname));
    }
}