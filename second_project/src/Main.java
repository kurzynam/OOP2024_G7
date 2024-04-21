import java.util.Iterator;

public class Main {
    public static void main(String[] args) {
        CustomList<String> newList = new CustomList<>();
        newList.addLast("Hello");
        //System.out.println(newList.getLast());
        newList.addLast("World");
        //System.out.println(newList.getLast());
        Iterator<String> iter = newList.iterator();
        String text = iter.next();
        System.out.println(text);
        while (iter.hasNext()){
            text = iter.next();
            System.out.println(text);
        }


    }

}