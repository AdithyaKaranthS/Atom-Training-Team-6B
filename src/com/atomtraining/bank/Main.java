package com.atomtraining.bank;

import java.util.List;
import java.util.Scanner;

// Console entry point and user interface for the banking application.
class Main {

    private static final Scanner scanner = new Scanner(System.in);
    // DATA TYPE: BankAccount is the application service that owns account state.
    private static final BankAccount account = new BankAccount(0);

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println(" Welcome to the Bank Account & Transaction Ledger");
        System.out.println(" Initialized demo account with $0.00 opening balance.");
        System.out.println("==========================================================");

        // DATA TYPE: boolean controls whether the menu loop continues running.
        boolean running = true;
        while (running) {
            displayMenu();
            switch (readIntInput("Enter your choice: ")) {
                case 1 ->
                    handleDeposit();
                case 2 ->
                    handleWithdraw();
                case 3 ->
                    handleCheckBalance();
                case 4 ->
                    handleMiniStatement();
                case 5 ->
                    handleUndo();
                case 6 -> {
                    System.out.println("\nThank you for using the Bank Account System. Goodbye!");
                    running = false;
                }
                default ->
                    System.out.println("\n[ERROR] Invalid choice! Please select an option between 1 and 6.");
            }
        }
        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("\n====================================");
        System.out.println("       BANK ACCOUNT SYSTEM          ");
        System.out.println("====================================");
        System.out.println("1. Deposit Money");
        System.out.println("2. Withdraw Money");
        System.out.println("3. Check Balance");
        System.out.println("4. Mini-Statement");
        System.out.println("5. Undo Last Transaction");
        System.out.println("6. Exit");
        System.out.println("====================================");
    }

    private static void handleDeposit() {
        System.out.println("\n--- Deposit Money ---");
        try {
            // DATA TYPE: Transaction contains the result of the deposit operation.
            Transaction txn = account.deposit(readDoubleInput("Enter deposit amount ($): "));
            System.out.println("\n[SUCCESS] Deposit successful!");
            System.out.printf("Deposited: $%,.2f | New Balance: $%,.2f | Transaction ID: #%d%n",
                    txn.getAmount(), txn.getBalanceAfterTransaction(), txn.getTransactionId());
        } catch (IllegalArgumentException e) {
            // EXCEPTION: Handles invalid amounts rejected by BankAccount.
            System.out.println("\n[ERROR] " + e.getMessage());
        }
    }

    private static void handleWithdraw() {
        System.out.println("\n--- Withdraw Money ---");
        try {
            // DATA TYPE: Transaction contains the result of the withdrawal operation.
            Transaction txn = account.withdraw(readDoubleInput("Enter withdrawal amount ($): "));
            System.out.println("\n[SUCCESS] Withdrawal successful!");
            System.out.printf("Withdrawn: $%,.2f | Remaining Balance: $%,.2f | Transaction ID: #%d%n",
                    txn.getAmount(), txn.getBalanceAfterTransaction(), txn.getTransactionId());
        } catch (IllegalArgumentException | IllegalStateException e) {
            // EXCEPTIONS: Multi-catch handles invalid amounts and insufficient funds.
            System.out.println("\n[ERROR] " + e.getMessage());
        }
    }

    private static void handleCheckBalance() {
        System.out.println("\n--- Account Balance ---");
        System.out.printf("Current Balance: $%,.2f%n", account.getBalance());
        System.out.printf("Active Transactions in Ledger: %d%n", account.getTransactionCount());
    }

    private static void handleMiniStatement() {
        System.out.println("\n--- Mini-Statement ---");
        if (account.getTransactionCount() == 0) {
            System.out.println("[INFO] No transactions found in the ledger yet.");
            return;
        }

        int n = readIntInput("Enter number of recent transactions (N): ");
        if (n <= 0) {
            System.out.println("[ERROR] N must be a positive integer greater than 0.");
            return;
        }

        // DATA TYPE: List<Transaction> contains the selected transaction records.
        List<Transaction> transactions = account.getLastNTransactions(n);
        System.out.println("\n" + "=".repeat(85));
        System.out.printf(" Displaying %d of %d active transactions (Chronological Order)%n",
                transactions.size(), account.getTransactionCount());
        System.out.println("=".repeat(85));
        System.out.printf("%-8s | %-10s | %-14s | %-18s | %-20s%n",
                "Txn ID", "Type", "Amount ($)", "Balance After ($)", "Timestamp");
        System.out.println("-".repeat(85));

        // STREAM API: forEach() applies this lambda to every transaction in the list.
        transactions.forEach(txn -> System.out.printf("%-8d | %-10s | %14s | %18s | %-20s%n",
                txn.getTransactionId(),
                txn.getTransactionType(),
                String.format("$%,.2f", txn.getAmount()),
                String.format("$%,.2f", txn.getBalanceAfterTransaction()),
                txn.getFormattedTimestamp()));
        System.out.println("=".repeat(85));
    }

    private static void handleUndo() {
        System.out.println("\n--- Undo Last Transaction ---");
        Transaction undone = account.undoLastTransaction();
        if (undone == null) {
            System.out.println("[WARNING] No transactions available to undo.");
            return;
        }
        System.out.println("\n[SUCCESS] Transaction successfully reversed!");
        System.out.printf("Reversed Transaction ID: #%d%n", undone.getTransactionId());
        System.out.printf("Original Operation    : %s of $%,.2f%n",
                undone.getTransactionType(), undone.getAmount());
        System.out.printf("Updated Balance       : $%,.2f%n", account.getBalance());
        System.out.println("Transaction has been removed from the active ledger and undo stack.");
    }

    // Reads and validates an integer from the console.
    private static int readIntInput(String prompt) {
        // DATA TYPE: int is returned after the input text is parsed.
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                // EXCEPTION: NumberFormatException means the text is not a valid integer.
                System.out.println("[ERROR] Invalid input. Please enter a valid integer.");
            }
        }
    }

    // Reads and validates a decimal number from the console.
    private static double readDoubleInput(String prompt) {
        // DATA TYPE: double is returned after the input text is parsed.
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                // EXCEPTION: NumberFormatException means the text is not a valid decimal.
                System.out.println("[ERROR] Invalid input. Please enter a valid decimal number.");
            }
        }
    }
}
