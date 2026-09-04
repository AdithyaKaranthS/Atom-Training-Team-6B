# Banking App

A simple Java console application that simulates a bank account and maintains a transaction ledger.

## Features

- Starts with a demo balance of `$0.00`.
- Deposit money into the account.
- Withdraw money when sufficient funds are available.
- Check the current balance and active transaction count.
- Display the most recent transactions in chronological order.
- Undo the most recent deposit or withdrawal using a custom LIFO stack.
- Validate invalid amounts, malformed input, negative opening balances, and insufficient funds.

## Project Structure

The application is split into focused source files:

- [Transaction.java](src/Transaction.java): Stores transaction details and timestamps.
- [CustomStack.java](src/CustomStack.java): Generic stack used to support undo operations.
- [BankAccount.java](src/BankAccount.java): Manages the balance, ledger, and transaction history.
- [Main.java](src/Main.java): Provides the interactive console menu and application entry point.

## Requirements

- Java Development Kit (JDK) 14 or newer.

The application uses switch expressions with arrow labels, which require Java 14 or newer.

## Run the Application

From the project directory, compile the source files into `src`:

```bash
javac -d src src/*.java
```

Then start the application:

```bash
java -cp src Main
```

## Menu Options

| Option | Action |
| --- | --- |
| 1 | Deposit money |
| 2 | Withdraw money |
| 3 | Check the balance |
| 4 | View a mini-statement of recent transactions |
| 5 | Undo the last transaction |
| 6 | Exit the application |

## Data Types Used

- `int`: Transaction IDs, transaction counts, stack size, and menu input.
- `double`: Account balances and transaction amounts.
- `String`: User prompts, messages, formatted currency, and timestamps.
- `enum Transaction.Type`: Represents `DEPOSIT` and `WITHDRAW` transaction types.
- `LocalDateTime`: Stores the date and time when each transaction is created.
- `LinkedHashMap<Integer, Transaction>`: Maintains the transaction ledger in insertion order.
- `List<Transaction>`: Stores the transactions returned for a mini-statement.
- `CustomStack<Transaction>`: Generic LIFO stack used to undo transactions.

## Exceptions Used

- `IllegalArgumentException`: Rejects negative opening balances, non-positive deposits or withdrawals, and invalid transaction counts.
- `InsufficientFundsException`: Reports withdrawal attempts when the account has insufficient funds.
- `NumberFormatException`: Handles text that cannot be converted into an integer or decimal number during user input.
- `EmptyStackException`: Prevents `CustomStack.pop()` and `CustomStack.peek()` from being used when the stack is empty.

## Stream API Used

`BankAccount.getLastNTransactions()` uses the Java Stream API to create a stream from the ledger, skip older transactions, and collect the requested results into a list:

- `stream()`: Creates a stream from the ledger values.
- `skip()`: Ignores older transactions when more than `N` transactions exist.
- `collect(Collectors.toList())`: Converts the stream results into a `List<Transaction>`.

The mini-statement uses `List.forEach()` with a lambda expression to print each transaction.

## Notes

- Transactions are stored in memory and are lost when the application exits.
- The account balance and transaction amounts use `double` for simplicity; production banking software should use `BigDecimal` for monetary values.
- Undo removes the reversed transaction from the active ledger and restores the previous balance.
- The account is currently initialized with `$0.00` in `Main.java`. The startup banner still displays a `$1,000.00` opening balance and should be updated in the source if that message is intended to be accurate.