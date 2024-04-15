public class CustomList<T> {

    private Node<T> head;
    private Node<T> tail;

    private int size = 0;

    public int getSize() {
        return size;
    }

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
    public void addFirst(T value){
        Node<T> newNode = new Node<>(value);
        if (head == null){
            tail = newNode;
        }
        newNode.next = head;
        head = newNode;
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


    public static class Node<T>{
        T value;
        Node<T> next;

        Node (T value){
            this.value = value;
            this.next = null;
        }
    }
}
