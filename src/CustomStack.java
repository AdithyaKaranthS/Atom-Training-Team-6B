import java.util.EmptyStackException;

// DATA TYPE: A generic stack can store any object type supplied as T.
class CustomStack<T> {

    // ENCAPSULATION: The stack's internal nodes and size are hidden behind push, pop, peek, and size methods.
    private static class Node<T> {

        // DATA TYPE: T is the value stored in this node.
        final T data;
        // DATA TYPE: Node<T> points to the next node in the linked stack.
        Node<T> next;

        // Creates a node with a value and its next-node reference.
        Node(T data, Node<T> next) {
            this.data = data;
            this.next = next;
        }
    }

    private Node<T> top;
    // DATA TYPE: int tracks how many items are currently in the stack.
    private int size;

    // Adds an item to the top of the stack.
    public void push(T item) {
        top = new Node<>(item, top);
        size++;
    }

    // Removes and returns the item at the top of the stack.
    public T pop() {
        if (isEmpty()) {
            // EXCEPTION: EmptyStackException prevents removing from an empty stack.
            throw new EmptyStackException();
        }
        T item = top.data;
        top = top.next;
        size--;
        return item;
    }

    // Returns the top item without removing it.
    public T peek() {
        if (isEmpty()) {
            // EXCEPTION: EmptyStackException prevents reading an empty stack.
            throw new EmptyStackException();
        }
        return top.data;
    }

    public boolean isEmpty() {
        // DATA TYPE: boolean reports whether the stack contains no nodes.
        return top == null;
    }

    // Returns the number of items currently stored.
    public int size() {
        return size;
    }
}
