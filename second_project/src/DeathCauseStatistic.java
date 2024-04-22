import java.util.function.Function;

public class DeathCauseStatistic {
    private String code;
    private int[] numberOfDeaths;

    public static DeathCauseStatistic fromCsvLine(String line){
        Function<String, String> removeTab = s -> s.replace("")
    }
}
