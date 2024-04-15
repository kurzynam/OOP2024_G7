import java.util.AbstractList;
import java.util.Iterator;

public class CustomList<T> extends AbstractList<T> {

    private Node<T> head;
    private Node<T> tail;

    private int size = 0;

    public Node<T> getHead() {
        return head;
    }

    public T getLast(){
        if (tail == null){
            return null;
        }
        return tail.value;
    }
    public T getFirst(){
        if (head == null){
            return null;
        }
        return head.value;
    }

    public T removeLast(){
        if (head == null){
            return null;
        }
        Node<T> temp = head;
        while(temp.next != tail){
            temp = temp.next;
        }
        T retVal = tail.value;
        tail = temp;
        size--;
        return retVal;
    }

    public T removeFirst(){
        if (head == null){
            return null;
        }
        T retValue = head.value;
        Node<T> temp = head.next;
        head = temp;
        size--;
        return retValue;

    }
    public void addFirst(T value){
        Node<T> newNode = new Node<>(value);
        if (head == null){
            tail = newNode;
        }
        newNode.next = head;
        head = newNode;
        size++;
    }
    public Node<T> getTail() {
        return tail;
    }


    public void addLast(T value){
        Node<T> newNode = new Node<T>(value);

        if (head == null){
            head = newNode;
        }else {
            tail.next = newNode;
        }
        tail = newNode;
        size++;
    }


    public Iterator<T> iterator(){
        return new Iterator<T>() {
            Node<T> current = head;
            @Override
            public boolean hasNext() {
                return current.next != null;
            }

            @Override
            public T next() {
                if (head == null){
                    return null;
                }
                T retVal = current.value;
                current = current.next;
                return retVal;
            }
        };
    }

    @Override
    public T get(int index) {
        if (index >= 0 && index < size){
            Node<T> temp = head;
            for (int i = 0 ; i < index; i++){
                temp = temp.next;
            }
            return temp.value;
        }
        else {
            return null;
        }
    }

    @Override
    public int size() {
        return size;
    }


    public static class Node<T>{
        T value;
        Node<T> next;

        Node (T value){
            this.value = value;
            this.next = null;
        }
    }
}
