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

- [Transaction.java](Transaction.java): Stores transaction details and timestamps.
- [CustomStack.java](CustomStack.java): Generic stack used to support undo operations.
- [BankAccount.java](BankAccount.java): Manages the balance, ledger, and transaction history.
- [Main.java](Main.java): Provides the interactive console menu and application entry point.

## Requirements

- Java Development Kit (JDK) 14 or newer.

The application uses switch expressions with arrow labels, which require Java 14 or newer.

## Run the Application

From the project directory, compile the source file:

```bash
javac *.java
```

Then start the application:

```bash
java Main
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

## Notes

- Transactions are stored in memory and are lost when the application exits.
- The account balance and transaction amounts use `double` for simplicity; production banking software should use `BigDecimal` for monetary values.
- Undo removes the reversed transaction from the active ledger and restores the previous balance.
- The account is currently initialized with `$0.00` in `Main.java`. The startup banner still displays a `$1,000.00` opening balance and should be updated in the source if that message is intended to be accurate.