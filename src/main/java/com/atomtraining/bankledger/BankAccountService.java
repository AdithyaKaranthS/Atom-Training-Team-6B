package com.atomtraining.bankledger;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.stereotype.Service;

/** Persistent account service. The in-memory stack is reconstructed from the database at startup. */
@Service
public class BankAccountService {
    private final CsvLedgerRepository ledgerRepository;
    private final LinkedHashMap<Integer, Transaction> ledger = new LinkedHashMap<>();
    private TransactionStack undoStack = new TransactionStack();
    private double balance;

    public BankAccountService(CsvLedgerRepository ledgerRepository) {
        this.ledgerRepository = ledgerRepository;
        rebuildFromDatabase();
    }

    private synchronized void rebuildFromDatabase() {
        ledger.clear();
        undoStack = new TransactionStack();
        for (Transaction transaction : ledgerRepository.load()) {
            ledger.put(transaction.getId(), transaction);
            balance = transaction.getBalanceAfterTransaction();
        }
        for (Transaction transaction : ledger.values()) undoStack.push(transaction);
    }

    public synchronized double getBalance() { return balance; }

    public synchronized List<Transaction> statement(int count) {
        if (count <= 0) throw new IllegalArgumentException("n must be a positive whole number");
        List<Transaction> all = new ArrayList<>(ledger.values());
        List<Transaction> newestFirst = new ArrayList<>();
        for (int i = all.size() - 1; i >= 0 && newestFirst.size() < count; i--) newestFirst.add(all.get(i));
        return newestFirst;
    }

    public synchronized Transaction deposit(double amount) {
        validateAmount(amount);
        return record(TransactionType.DEPOSIT, amount, balance + amount);
    }

    public synchronized Transaction withdraw(double amount) {
        validateAmount(amount);
        if (amount > balance) throw new IllegalArgumentException("Insufficient balance");
        return record(TransactionType.WITHDRAW, amount, balance - amount);
    }

    public synchronized Transaction undo() {
        if (undoStack.isEmpty()) throw new IllegalArgumentException("No transactions to undo");
        Transaction transaction = undoStack.pop();
        double restoredBalance = transaction.getType() == TransactionType.DEPOSIT
                ? balance - transaction.getAmount()
                : balance + transaction.getAmount();
        balance = restoredBalance;
        ledger.remove(transaction.getId());
        ledgerRepository.save(ledger.values());
        return transaction;
    }

    private Transaction record(TransactionType type, double amount, double newBalance) {
        int id = ledger.isEmpty() ? 1 : ledger.keySet().stream().mapToInt(Integer::intValue).max().orElse(0) + 1;
        Transaction transaction = new Transaction(id, type, amount, newBalance);
        balance = newBalance;
        ledger.put(id, transaction);
        undoStack.push(transaction);
        ledgerRepository.save(ledger.values());
        return transaction;
    }

    private void validateAmount(double amount) {
        if (!Double.isFinite(amount) || amount <= 0) throw new IllegalArgumentException("Amount must be positive");
    }
}
