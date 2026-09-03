# Bank Account & Transaction Ledger

A console-based Java banking application demonstrating data structures, file persistence, object-oriented design, and custom exception handling.

## Features

- Deposit money and record the resulting balance.
- Withdraw money only when sufficient funds are available.
- Check the current account balance.
- Display the last `N` transactions in reverse chronological order.
- Show total deposits and withdrawals in the mini-statement.
- Undo the most recent transaction using a custom LIFO stack.
- Persist transactions between sessions in a CSV file.
- Continue running after invalid menu choices, invalid amounts, insufficient funds, empty-ledger operations, or persistence errors.
- Report and skip malformed CSV rows without discarding valid rows.

## Run

From the project root with JDK 8 or newer:

```text
javac -d out src/*.java
java -cp out Main
```

The application displays this menu:

```text
1. Deposit
2. Withdraw
3. Check Balance
4. Mini-Statement
5. Undo Last Transaction
6. Exit
```

## Persistence

Transactions are stored in `transactions.csv` in the project root using this format:

```text
transactionId,type,amount,balanceAfter
```

The file is loaded at startup. The ledger, custom stack, current balance, and next transaction ID are rebuilt from the saved rows. Deposit, withdrawal, and undo operations rewrite the CSV so it reflects the current ledger. A missing or empty file starts a fresh account with a balance of `0.00`.

## DSA And Design

- `LinkedHashMap<Integer, Transaction>` preserves transaction insertion order for the ledger.
- `TransactionStack` is implemented from scratch with linked nodes and provides LIFO access for undo operations. It supports `push`, `pop`, `peek`, `isEmpty`, and `clear`.
- `Stream API` selects the newest `N` transactions for mini-statements and calculates deposit and withdrawal totals.
- `BigDecimal` stores monetary values to avoid floating-point rounding problems.
- `CSVManager` owns CSV loading and saving.
- `BankAccount` owns balance changes, transaction creation, ledger state, and undo behavior.

The source also includes comments documenting each declared data structure and a header summary of the ledger map, custom stack, Stream API, and CSV persistence file.

## Custom Exceptions

- `InvalidAmountException`: the amount is missing, non-numeric, zero, or negative.
- `InsufficientBalanceException`: a withdrawal exceeds the current balance.
- `EmptyLedgerException`: a mini-statement or undo is requested with no transactions.
- `TransactionNotFoundException`: an undo operation cannot find its transaction in the ledger.
- `CorruptedLedgerFileException`: a CSV row has invalid fields or values.
- `LedgerPersistenceException`: the CSV file cannot be read, inspected, or saved.

All exceptions are handled with user-friendly messages so the program returns to the menu instead of printing a raw stack trace.
