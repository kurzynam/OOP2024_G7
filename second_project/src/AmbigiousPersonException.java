public class AmbigiousPersonException extends Exception{
    public AmbigiousPersonException(String fullname) {
        super(String.format("Osoba o imieniu i nazwisku %s występuje wielokrotnie!", fullname));
    }
}