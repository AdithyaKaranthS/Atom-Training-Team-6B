# Bank Account & Transaction Ledger

A Java Swing desktop application for managing a simple bank account. It supports deposits, withdrawals, balance checking, a reverse-chronological mini-statement, and undoing the most recent transaction.

The project demonstrates core Java OOP concepts along with `LinkedHashMap` and a custom linked-list stack. It does **not** use `java.util.Stack`.

## Features

- Deposit a positive amount in rupees.
- Withdraw a positive amount when sufficient balance is available.
- See the current balance in the persistent header and balance screen.
- View the last **N** transactions in a mini-statement table.
- Undo the latest successful deposit or withdrawal.
- Receive success and error messages directly in the interface instead of popup dialogs.
- Display all monetary values using `Rs.`.

## Requirements

- Java Development Kit (JDK) 17 or later
- No external libraries are required

## Run the application

Open PowerShell or Command Prompt in this project folder and run:

```powershell
javac *.java
java MainGUI
```

## Project structure

| File | Purpose |
| --- | --- |
| `MainGUI.java` | Swing user interface, navigation, forms, status messages, and transaction table. This is the main entry point. |
| `BankAccount.java` | Account business logic: deposit, withdraw, ledger maintenance, and undo. |
| `Transaction.java` | Immutable object that stores one transaction's ID, type, amount, and balance after completion. |
| `TransactionType.java` | Enum containing the valid types: `DEPOSIT` and `WITHDRAW`. |
| `TransactionStack.java` | Custom linked-list stack used for LIFO undo functionality. |
| `BankAccountLedger.java` | Compatibility launcher that opens `MainGUI`. |

## How the transaction ledger works

`BankAccount` maintains the history using:

```java
LinkedHashMap<Integer, Transaction> ledger
```

The key is the transaction ID and the value is the matching `Transaction` object. `LinkedHashMap` preserves insertion order, so the application always knows the order in which transactions occurred.

For the mini-statement, the ledger values are copied into a list and read from the final item back to the first item. This shows the newest transaction first.

## How undo works

Every successful transaction is saved in two places:

1. The `LinkedHashMap` ledger, for statement/history display.
2. The custom `TransactionStack`, for undo behavior.

The stack uses LIFO (Last In, First Out): the most recently completed transaction is at the top. When Undo is selected:

- `pop()` removes the latest transaction from the custom stack.
- A previous deposit is reversed by subtracting its amount.
- A previous withdrawal is reversed by adding its amount back.
- The transaction is removed from the ledger.

The custom stack supports `push()`, `pop()`, `peek()`, and `isEmpty()`.

## User interface

The interface uses a `JFrame` with:

- A header that permanently displays the available balance.
- A left-side navigation panel.
- A right-side `CardLayout` that switches between Deposit, Withdraw, Check Balance, Mini-Statement, and Undo screens.
- Inline green success messages and red validation/error messages.

## Validation handled

- Empty or non-numeric amounts
- Zero or negative amounts
- Insufficient balance for withdrawals
- Invalid mini-statement count
- Undo requested when no transaction exists
