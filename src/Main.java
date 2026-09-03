/*
 * Data structures and APIs used:
 * - LinkedHashMap stores transactions in insertion order for the ledger.
 * - TransactionStack provides LIFO access for undo operations.
 * - Stream API filters, sorts, limits, and summarizes ledger transactions.
 * - CSV file persists the ledger between application runs.
 */

import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    private static final String CSV_FILE = "transactions.csv";

    public static void main(String[] args) {
        CSVManager csvManager = new CSVManager(Paths.get(CSV_FILE));
        BankAccount account;
        try {
            account = new BankAccount(csvManager.load());
        } catch (LedgerPersistenceException exception) {
            System.out.println("Could not load saved data: " + exception.getMessage());
            System.out.println("Starting with a fresh account.");
            account = new BankAccount(new LinkedHashMap<>());
        }

        System.out.println("Loaded balance: " + BankAccount.format(account.getBalance()));
        try (Scanner scanner = new Scanner(System.in)) {
            runMenu(scanner, account, csvManager);
        }
        System.out.println("Thank you for using the banking application.");
    }

    private static void runMenu(Scanner scanner, BankAccount account, CSVManager csvManager) {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1":
                        changeBalance(scanner, account, csvManager, true);
                        break;
                    case "2":
                        changeBalance(scanner, account, csvManager, false);
                        break;
                    case "3":
                        System.out.println("Current balance: " + BankAccount.format(account.getBalance()));
                        break;
                    case "4":
                        printMiniStatement(scanner, account);
                        break;
                    case "5":
                        undo(account, csvManager);
                        break;
                    case "6":
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid menu choice. Please select a number from 1 to 6.");
                }
            } catch (InvalidAmountException | InsufficientBalanceException | EmptyLedgerException
                     | TransactionNotFoundException | LedgerPersistenceException exception) {
                System.out.println("Error: " + exception.getMessage());
            }
        }
    }

    private static void changeBalance(Scanner scanner, BankAccount account, CSVManager csvManager,
                                      boolean deposit)
            throws InvalidAmountException, InsufficientBalanceException, LedgerPersistenceException {
        BigDecimal amount = readAmount(scanner);
        Transaction transaction = deposit ? account.deposit(amount) : account.withdraw(amount);
        csvManager.save(account.getLedger());
        System.out.println((deposit ? "Deposit" : "Withdrawal")
                + " successful. Transaction ID: " + transaction.getId());
        System.out.println("Balance: " + BankAccount.format(account.getBalance()));
    }

    private static void undo(BankAccount account, CSVManager csvManager)
            throws EmptyLedgerException, TransactionNotFoundException, LedgerPersistenceException {
        Transaction transaction = account.undoLastTransaction();
        csvManager.save(account.getLedger());
        System.out.println("Undid transaction " + transaction.getId() + " (" + transaction.getType() + ").");
        System.out.println("Balance: " + BankAccount.format(account.getBalance()));
    }

    private static void printMiniStatement(Scanner scanner, BankAccount account)
            throws EmptyLedgerException {
        if (account.getLedger().isEmpty()) {
            throw new EmptyLedgerException("There are no transactions for a mini-statement.");
        }
        System.out.print("How many transactions should be displayed? ");
        int count;
        try {
            count = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException exception) {
            System.out.println("Error: N must be a positive whole number.");
            return;
        }
        if (count <= 0) {
            System.out.println("Error: N must be a positive whole number.");
            return;
        }

        // Stream the newest transactions first and limit the result to the requested count.
        // List holds the newest transactions selected for display.
        List<Transaction> transactions = account.getLedger().values().stream()
            .sorted((left, right) -> Integer.compare(right.getId(), left.getId()))
            .limit(count)
            .collect(Collectors.toList());
        System.out.println("ID | TYPE     | AMOUNT | BALANCE AFTER");
        for (Transaction transaction : transactions) {
            System.out.printf("%d | %-8s | %6s | %13s%n", transaction.getId(), transaction.getType(),
                    BankAccount.format(transaction.getAmount()),
                    BankAccount.format(transaction.getBalanceAfter()));
        }

        BigDecimal totalDeposits = account.getLedger().values().stream()
            .filter(transaction -> transaction.getType() == TransactionType.DEPOSIT)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalWithdrawals = account.getLedger().values().stream()
            .filter(transaction -> transaction.getType() == TransactionType.WITHDRAW)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("Total deposits: " + BankAccount.format(totalDeposits));
        System.out.println("Total withdrawals: " + BankAccount.format(totalWithdrawals));
    }

    private static BigDecimal readAmount(Scanner scanner) throws InvalidAmountException {
        System.out.print("Enter amount: ");
        String input = scanner.nextLine().trim();
        try {
            return new BigDecimal(input);
        } catch (NumberFormatException exception) {
            throw new InvalidAmountException("Amount must be a positive number.");
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("===== BANK ACCOUNT & TRANSACTION LEDGER =====");
        System.out.println("1. Deposit");
        System.out.println("2. Withdraw");
        System.out.println("3. Check Balance");
        System.out.println("4. Mini-Statement");
        System.out.println("5. Undo Last Transaction");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    }
}
