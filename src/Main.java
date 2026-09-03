import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
            account = new BankAccount(new java.util.LinkedHashMap<>());
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
                        deposit(scanner, account, csvManager);
                        break;
                    case "2":
                        withdraw(scanner, account, csvManager);
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

    private static void deposit(Scanner scanner, BankAccount account, CSVManager csvManager)
            throws InvalidAmountException, LedgerPersistenceException {
        BigDecimal amount = readAmount(scanner);
        Transaction transaction = account.deposit(amount);
        csvManager.save(account.getLedger());
        System.out.println("Deposit successful. Transaction ID: " + transaction.getId());
        System.out.println("Balance: " + BankAccount.format(account.getBalance()));
    }

    private static void withdraw(Scanner scanner, BankAccount account, CSVManager csvManager)
            throws InvalidAmountException, InsufficientBalanceException, LedgerPersistenceException {
        BigDecimal amount = readAmount(scanner);
        Transaction transaction = account.withdraw(amount);
        csvManager.save(account.getLedger());
        System.out.println("Withdrawal successful. Transaction ID: " + transaction.getId());
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

        List<Transaction> transactions = new ArrayList<>(account.getLedger().values());
        int firstIndex = Math.max(0, transactions.size() - count);
        System.out.println("ID | TYPE     | AMOUNT | BALANCE AFTER");
        for (int index = transactions.size() - 1; index >= firstIndex; index--) {
            Transaction transaction = transactions.get(index);
            System.out.printf("%d | %-8s | %6s | %13s%n", transaction.getId(), transaction.getType(),
                    BankAccount.format(transaction.getAmount()),
                    BankAccount.format(transaction.getBalanceAfter()));
        }
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
