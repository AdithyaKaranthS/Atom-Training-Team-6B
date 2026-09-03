import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;

public class BankAccount {
    private final LinkedHashMap<Integer, Transaction> ledger;
    private final TransactionStack stack;
    private BigDecimal balance;
    private int nextTransactionId;

    public BankAccount(LinkedHashMap<Integer, Transaction> loadedLedger) {
        ledger = loadedLedger;
        stack = new TransactionStack();
        balance = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
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
        balance = ledger.isEmpty()
                ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : getLastTransaction().getBalanceAfter();
        nextTransactionId = ledger.isEmpty() ? 1 : getLastTransaction().getId() + 1;
        return transaction;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public LinkedHashMap<Integer, Transaction> getLedger() {
        return ledger;
    }

    public TransactionStack getStack() {
        return stack;
    }

    public Transaction getLastTransaction() throws EmptyLedgerException {
        if (ledger.isEmpty()) {
            throw new EmptyLedgerException("There are no transactions recorded.");
        }
        Transaction last = null;
        for (Transaction transaction : ledger.values()) {
            last = transaction;
        }
        return last;
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

    public static String format(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
