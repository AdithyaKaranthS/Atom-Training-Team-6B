package com.atomtraining.bank;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// DATA TYPE: Transaction is the data model for one deposit or withdrawal.
class Transaction {

    // ENCAPSULATION: Private final fields protect transaction data from direct external modification.
    public enum Type {
        DEPOSIT, WITHDRAW
    }

    // DATA TYPE: int identifies the transaction.
    private final int transactionId;
    // DATA TYPE: Type stores whether this transaction is a deposit or withdrawal.
    private final Type transactionType;
    // DATA TYPE: double stores the transaction amount.
    private final double amount;
    // DATA TYPE: double stores the balance after this transaction.
    private final double balanceAfterTransaction;
    // DATA TYPE: LocalDateTime records when the transaction was created.
    private final LocalDateTime timestamp = LocalDateTime.now();
    // DATA TYPE: DateTimeFormatter converts the timestamp to display text.
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Creates an immutable transaction record.
    public Transaction(int transactionId, Type transactionType, double amount, double balanceAfterTransaction) {
        this.transactionId = transactionId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balanceAfterTransaction = balanceAfterTransaction;
    }

    // Returns the transaction ID.
    public int getTransactionId() {
        return transactionId;
    }

    // Returns the deposit or withdrawal type.
    public Type getTransactionType() {
        return transactionType;
    }

    // Returns the transaction amount.
    public double getAmount() {
        return amount;
    }

    // Returns the account balance after this transaction.
    public double getBalanceAfterTransaction() {
        return balanceAfterTransaction;
    }

    // Returns the creation timestamp.
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getFormattedTimestamp() {
        return timestamp.format(FORMATTER);
    }

    // POLYMORPHISM: This implementation overrides Object.toString() and is selected when a Transaction is displayed as text.
    @Override
    public String toString() {
        return String.format("[Txn ID: %d] %-8s | Amount: $%,.2f | Balance: $%,.2f | Time: %s",
                transactionId, transactionType, amount, balanceAfterTransaction, getFormattedTimestamp());
    }
}
