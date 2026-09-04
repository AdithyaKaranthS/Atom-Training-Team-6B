import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class Transaction {

    public enum Type {
        DEPOSIT, WITHDRAW
    }

    private final int transactionId;
    private final Type transactionType;
    private final double amount;
    private final double balanceAfterTransaction;
    private final LocalDateTime timestamp = LocalDateTime.now();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Transaction(int transactionId, Type transactionType, double amount, double balanceAfterTransaction) {
        this.transactionId = transactionId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balanceAfterTransaction = balanceAfterTransaction;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public Type getTransactionType() {
        return transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfterTransaction() {
        return balanceAfterTransaction;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getFormattedTimestamp() {
        return timestamp.format(FORMATTER);
    }

    @Override
    public String toString() {
        return String.format("[Txn ID: %d] %-8s | Amount: $%,.2f | Balance: $%,.2f | Time: %s",
                transactionId, transactionType, amount, balanceAfterTransaction, getFormattedTimestamp());
    }
}
