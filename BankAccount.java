import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

// Manages the account balance and the active transaction history.
class BankAccount {

    // DATA TYPE: double stores the account balance.
    private double balance;
    // DATA TYPES: LinkedHashMap preserves transaction insertion order; Integer is the key type.
    private final LinkedHashMap<Integer, Transaction> transactionLedger = new LinkedHashMap<>();
    // DATA TYPE: CustomStack<Transaction> stores transactions in LIFO order for undo operations.
    private final CustomStack<Transaction> undoStack = new CustomStack<>();
    // DATA TYPE: int is used for the next transaction ID.
    private int transactionIdCounter = 0;

    // Starts a new account with a zero balance.
    public BankAccount() {
        this(0.0);
    }

    // Creates an account with the supplied opening balance.
    public BankAccount(double initialBalance) {
        if (initialBalance < 0) {
            // EXCEPTION: IllegalArgumentException rejects an invalid opening balance.
            throw new IllegalArgumentException("Initial balance cannot be negative.");
        }
        this.balance = initialBalance;
    }

    // Adds money, records the transaction, and places it on the undo stack.
    public Transaction deposit(double amount) {
        if (amount <= 0) {
            // EXCEPTION: IllegalArgumentException rejects zero or negative deposits.
            throw new IllegalArgumentException("Deposit amount must be greater than zero.");
        }
        balance += amount;
        // DATA TYPE: Transaction represents one completed deposit.
        Transaction txn = new Transaction(++transactionIdCounter, Transaction.Type.DEPOSIT, amount, balance);
        transactionLedger.put(txn.getTransactionId(), txn);
        undoStack.push(txn);
        return txn;
    }

    // Removes money after validating the balance and records the withdrawal.
    public Transaction withdraw(double amount) {
        if (amount <= 0) {
            // EXCEPTION: IllegalArgumentException rejects zero or negative withdrawals.
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero.");
        }
        if (amount > balance) {
            // EXCEPTION: IllegalStateException reports insufficient available funds.
            throw new IllegalStateException(String.format(
                    "Insufficient funds! Current balance is $%,.2f, but attempted to withdraw $%,.2f.", balance, amount));
        }
        balance -= amount;
        Transaction txn = new Transaction(++transactionIdCounter, Transaction.Type.WITHDRAW, amount, balance);
        transactionLedger.put(txn.getTransactionId(), txn);
        undoStack.push(txn);
        return txn;
    }

    // Returns the current balance as a double value.
    public double getBalance() {
        return balance;
    }

    // Reverses the most recent transaction, if one exists.
    public Transaction undoLastTransaction() {
        if (undoStack.isEmpty()) {
            // No exception is needed here; null signals that there is nothing to undo.
            return null;
        }
        Transaction lastTxn = undoStack.pop();
        // DATA TYPE: The enum value determines whether the balance is restored or reduced.
        balance += (lastTxn.getTransactionType() == Transaction.Type.DEPOSIT) ? -lastTxn.getAmount() : lastTxn.getAmount();
        transactionLedger.remove(lastTxn.getTransactionId());
        return lastTxn;
    }

    public List<Transaction> getLastNTransactions(int n) {
        if (n <= 0) {
            // EXCEPTION: IllegalArgumentException requires a positive transaction count.
            throw new IllegalArgumentException("N must be a positive integer greater than 0.");
        }
        // STREAM API: stream() reads ledger values, skip() removes older entries,
        // and collect() converts the selected stream elements into a List<Transaction>.
        return transactionLedger.values().stream()
                .skip(Math.max(0, transactionLedger.size() - n))
                .collect(Collectors.toList());
    }

    // Returns the number of active transactions in the ledger.
    public int getTransactionCount() {
        return transactionLedger.size();
    }
}
