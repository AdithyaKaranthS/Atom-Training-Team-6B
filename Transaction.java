/** Immutable record of one completed banking operation. */
public class Transaction {
    private final int id;
    private final TransactionType type;
    private final double amount;
    private final double balanceAfterTransaction;

    public Transaction(int id, TransactionType type, double amount, double balanceAfterTransaction) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.balanceAfterTransaction = balanceAfterTransaction;
    }
    public int getId() { return id; }
    public TransactionType getType() { return type; }
    public double getAmount() { return amount; }
    public double getBalanceAfterTransaction() { return balanceAfterTransaction; }
}
