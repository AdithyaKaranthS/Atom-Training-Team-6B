import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;

public class BankAccount {
    // LinkedHashMap stores transactions by ID while preserving insertion order.
    private final LinkedHashMap<Integer, Transaction> ledger;
    // TransactionStack stores transactions in LIFO order for undo operations.
    private final TransactionStack stack;
    private BigDecimal balance;
    private int nextTransactionId;

    public BankAccount(LinkedHashMap<Integer, Transaction> loadedLedger) {
        ledger = loadedLedger;
        stack = new TransactionStack();
        balance = zeroBalance();
        nextTransactionId = 1;
        rebuildState();
    }

    public Transaction deposit(BigDecimal amount) throws InvalidAmountException {
        validateAmount(amount);
        balance = balance.add(amount);
        return record(TransactionType.DEPOSIT, amount);
    }

    public Transaction withdraw(BigDecimal amount)
            throws InvalidAmountException, InsufficientBalanceException {
        validateAmount(amount);
        if (amount.compareTo(balance) > 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance. Available balance: " + format(balance));
        }
        balance = balance.subtract(amount);
        return record(TransactionType.WITHDRAW, amount);
    }

    public Transaction undoLastTransaction()
            throws EmptyLedgerException, TransactionNotFoundException {
        Transaction transaction = stack.pop();
        Transaction removed = ledger.remove(transaction.getId());
        if (removed == null) {
            throw new TransactionNotFoundException(
                    "Transaction " + transaction.getId() + " was not found in the ledger.");
        }
        balance = transaction.getType() == TransactionType.DEPOSIT
                ? balance.subtract(transaction.getAmount())
                : balance.add(transaction.getAmount());
        return transaction;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public LinkedHashMap<Integer, Transaction> getLedger() {
        return ledger;
    }

    private Transaction record(TransactionType type, BigDecimal amount) {
        Transaction transaction = new Transaction(nextTransactionId++, type, amount, balance);
        ledger.put(transaction.getId(), transaction);
        stack.push(transaction);
        return transaction;
    }

    private void rebuildState() {
        stack.clear();
        for (Transaction transaction : ledger.values()) {
            stack.push(transaction);
            balance = transaction.getBalanceAfter();
            nextTransactionId = Math.max(nextTransactionId, transaction.getId() + 1);
        }
    }

    private void validateAmount(BigDecimal amount) throws InvalidAmountException {
        if (amount == null || amount.signum() <= 0) {
            throw new InvalidAmountException("Amount must be a positive number.");
        }
    }

    private static BigDecimal zeroBalance() {
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    public static String format(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
