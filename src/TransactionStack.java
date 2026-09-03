public class TransactionStack {
    private static final class Node {
        private final Transaction value;
        private final Node next;

        private Node(Transaction value, Node next) {
            this.value = value;
            this.next = next;
        }
    }

    private Node top;

    public void push(Transaction transaction) {
        top = new Node(transaction, top);
    }

    public Transaction pop() throws EmptyLedgerException {
        if (isEmpty()) {
            throw new EmptyLedgerException("There are no transactions to undo.");
        }
        Transaction transaction = top.value;
        top = top.next;
        return transaction;
    }

    public Transaction peek() throws EmptyLedgerException {
        if (isEmpty()) {
            throw new EmptyLedgerException("There are no transactions to undo.");
        }
        return top.value;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public void clear() {
        top = null;
    }
}
