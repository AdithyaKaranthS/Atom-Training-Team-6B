package com.atomtraining.bankledger;

/** Hand-written linked-list stack. java.util.Stack is not used. */
public class TransactionStack {
    private Node top;

    public void push(Transaction transaction) { top = new Node(transaction, top); }
    public Transaction pop() {
        if (isEmpty()) throw new IllegalStateException("No transactions to undo");
        Transaction transaction = top.transaction;
        top = top.next;
        return transaction;
    }
    public Transaction peek() {
        if (isEmpty()) throw new IllegalStateException("No transactions to view");
        return top.transaction;
    }
    public boolean isEmpty() { return top == null; }

    private static class Node {
        private final Transaction transaction;
        private final Node next;
        private Node(Transaction transaction, Node next) { this.transaction = transaction; this.next = next; }
    }
}
