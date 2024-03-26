public class NegativeLifespanException extends Exception{
    public NegativeLifespanException(String name) {
        super(String.format("Person %s has date of birth later than date of death", name));
    }
}
