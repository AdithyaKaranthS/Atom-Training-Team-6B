import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;

public class CSVManager {
    private static final String HEADER = "transactionId,type,amount,balanceAfter";
    // Path identifies the CSV file used to persist ledger transactions.
    private final Path file;

    public CSVManager(Path file) {
        this.file = file;
    }

    public LinkedHashMap<Integer, Transaction> load() throws LedgerPersistenceException {
        LinkedHashMap<Integer, Transaction> ledger = new LinkedHashMap<>();
        if (!Files.exists(file)) {
            return ledger;
        }

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String row;
            int rowNumber = 0;
            while ((row = reader.readLine()) != null) {
                rowNumber++;
                if (rowNumber == 1 && row.trim().equalsIgnoreCase(HEADER)) {
                    continue;
                }
                if (row.trim().isEmpty()) {
                    continue;
                }
                try {
                    Transaction transaction = parseRow(row);
                    if (ledger.containsKey(transaction.getId())) {
                        throw new CorruptedLedgerFileException("duplicate transaction ID " + transaction.getId());
                    }
                    ledger.put(transaction.getId(), transaction);
                } catch (CorruptedLedgerFileException | IllegalArgumentException exception) {
                    System.err.println("Skipped malformed CSV row " + rowNumber + ": " + exception.getMessage());
                }
            }
        } catch (IOException exception) {
            throw new LedgerPersistenceException("Could not read the transaction ledger.", exception);
        }
        return ledger;
    }

    public void save(LinkedHashMap<Integer, Transaction> ledger) throws LedgerPersistenceException {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writer.write(HEADER);
            writer.newLine();
            for (Transaction transaction : ledger.values()) {
                writer.write(transaction.toCsvRow());
                writer.newLine();
            }
        } catch (IOException exception) {
            throw new LedgerPersistenceException("Could not save the transaction ledger.", exception);
        }
    }

    private Transaction parseRow(String row) throws CorruptedLedgerFileException {
        String[] fields = row.split(",", -1);
        if (fields.length != 4) {
            throw new CorruptedLedgerFileException("expected 4 comma-separated fields");
        }
        try {
            int id = Integer.parseInt(fields[0].trim());
            TransactionType type = TransactionType.valueOf(fields[1].trim().toUpperCase());
            BigDecimal amount = new BigDecimal(fields[2].trim());
            BigDecimal balanceAfter = new BigDecimal(fields[3].trim());
            if (id <= 0 || amount.signum() <= 0 || balanceAfter.signum() < 0) {
                throw new CorruptedLedgerFileException("invalid ID, amount, or balance");
            }
            return new Transaction(id, type, amount, balanceAfter);
        } catch (IllegalArgumentException exception) {
            throw new CorruptedLedgerFileException("invalid numeric value or transaction type");
        }
    }
}
