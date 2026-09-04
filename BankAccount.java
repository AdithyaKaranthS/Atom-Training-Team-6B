import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

class BankAccount {

    private double balance;
    private final LinkedHashMap<Integer, Transaction> transactionLedger = new LinkedHashMap<>();
    private final CustomStack<Transaction> undoStack = new CustomStack<>();
    private int transactionIdCounter = 0;

    public BankAccount() {
        this(0.0);
    }

    public BankAccount(double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative.");
        }
        this.balance = initialBalance;
    }

    public Transaction deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero.");
        }
        balance += amount;
        Transaction txn = new Transaction(++transactionIdCounter, Transaction.Type.DEPOSIT, amount, balance);
        transactionLedger.put(txn.getTransactionId(), txn);
        undoStack.push(txn);
        return txn;
    }

    public Transaction withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero.");
        }
        if (amount > balance) {
            throw new IllegalStateException(String.format(
                    "Insufficient funds! Current balance is $%,.2f, but attempted to withdraw $%,.2f.", balance, amount));
        }
        balance -= amount;
        Transaction txn = new Transaction(++transactionIdCounter, Transaction.Type.WITHDRAW, amount, balance);
        transactionLedger.put(txn.getTransactionId(), txn);
        undoStack.push(txn);
        return txn;
    }

    public double getBalance() {
        return balance;
    }

    public Transaction undoLastTransaction() {
        if (undoStack.isEmpty()) {
            return null;
        }
        Transaction lastTxn = undoStack.pop();
        balance += (lastTxn.getTransactionType() == Transaction.Type.DEPOSIT) ? -lastTxn.getAmount() : lastTxn.getAmount();
        transactionLedger.remove(lastTxn.getTransactionId());
        return lastTxn;
    }

    public List<Transaction> getLastNTransactions(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("N must be a positive integer greater than 0.");
        }
        return transactionLedger.values().stream()
                .skip(Math.max(0, transactionLedger.size() - n))
                .collect(Collectors.toList());
    }

    public int getTransactionCount() {
        return transactionLedger.size();
    }
}
