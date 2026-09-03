import java.util.LinkedHashMap;

/** Business logic shared by the Swing screens. */
public class BankAccount {
    private final LinkedHashMap<Integer, Transaction> ledger = new LinkedHashMap<>();
    private final TransactionStack undoStack = new TransactionStack();
    private double balance;
    private int nextTransactionId = 1;

    public double getBalance() { return balance; }
    public LinkedHashMap<Integer, Transaction> getLedger() { return ledger; }
    public Transaction deposit(double amount) {
        validateAmount(amount);
        balance += amount;
        return record(TransactionType.DEPOSIT, amount);
    }
    public Transaction withdraw(double amount) {
        validateAmount(amount);
        if (amount > balance) throw new IllegalArgumentException("Insufficient balance");
        balance -= amount;
        return record(TransactionType.WITHDRAW, amount);
    }
    public Transaction undoLastTransaction() {
        if (undoStack.isEmpty()) return null;
        Transaction transaction = undoStack.pop();
        balance += transaction.getType() == TransactionType.DEPOSIT ? -transaction.getAmount() : transaction.getAmount();
        ledger.remove(transaction.getId());
        return transaction;
    }
    private Transaction record(TransactionType type, double amount) {
        Transaction transaction = new Transaction(nextTransactionId++, type, amount, balance);
        ledger.put(transaction.getId(), transaction);
        undoStack.push(transaction);
        return transaction;
    }
    private void validateAmount(double amount) {
        if (!Double.isFinite(amount) || amount <= 0) throw new IllegalArgumentException("Amount must be positive");
    }
}
