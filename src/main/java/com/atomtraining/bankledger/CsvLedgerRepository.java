package com.atomtraining.bankledger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class CsvLedgerRepository {
    private static final Path LEDGER_FILE = Path.of("data", "ledger.csv");
    private static final String HEADER = "id,type,amount,balanceAfterTransaction";

    public synchronized List<Transaction> load() {
        if (!Files.exists(LEDGER_FILE)) return new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(LEDGER_FILE);
            List<Transaction> transactions = new ArrayList<>();
            for (int lineNumber = 1; lineNumber < lines.size(); lineNumber++) {
                String[] fields = lines.get(lineNumber).split(",", -1);
                if (fields.length != 4) throw new IllegalStateException("Invalid ledger row at line " + (lineNumber + 1));
                transactions.add(new Transaction(
                        Integer.parseInt(fields[0]),
                        TransactionType.valueOf(fields[1]),
                        Double.parseDouble(fields[2]),
                        Double.parseDouble(fields[3])));
            }
            return transactions;
        } catch (IOException | RuntimeException exception) {
            throw new IllegalStateException("Unable to read the transaction ledger", exception);
        }
    }

    public synchronized void save(Iterable<Transaction> transactions) {
        try {
            Files.createDirectories(LEDGER_FILE.getParent());
            Path temporaryFile = LEDGER_FILE.resolveSibling("ledger.csv.tmp");
            List<String> lines = new ArrayList<>();
            lines.add(HEADER);
            for (Transaction transaction : transactions) {
                lines.add(String.join(",", String.valueOf(transaction.getId()), transaction.getType().name(),
                        Double.toString(transaction.getAmount()), Double.toString(transaction.getBalanceAfterTransaction())));
            }
            Files.write(temporaryFile, lines);
            try {
                Files.move(temporaryFile, LEDGER_FILE, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException atomicMoveUnsupported) {
                Files.move(temporaryFile, LEDGER_FILE, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to save the transaction ledger", exception);
        }
    }
}