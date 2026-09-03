/** Linked-list custom stack; java.util.Stack is intentionally not used. */
public class TransactionStack {
    private Node top;
    public void push(Transaction transaction) { top = new Node(transaction, top); }
    public Transaction pop() {
        if (isEmpty()) throw new IllegalStateException("Cannot pop an empty transaction stack.");
        Transaction transaction = top.transaction;
        top = top.next;
        return transaction;
    }
    public Transaction peek() {
        if (isEmpty()) throw new IllegalStateException("Cannot peek at an empty transaction stack.");
        return top.transaction;
    }
    public boolean isEmpty() { return top == null; }
    private static class Node {
        private final Transaction transaction;
        private final Node next;
        private Node(Transaction transaction, Node next) { this.transaction = transaction; this.next = next; }
    }
}
