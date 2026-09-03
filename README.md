# Bank Account & Transaction Ledger

A persistent Spring Boot web application for managing a simple bank account. It supports deposits, withdrawals, balance checking, a reverse-chronological mini-statement, and undoing the most recent transaction.

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
- Maven 3.9 or later

## Run over HTTPS

Generate the local self-signed certificate once from the project folder. The password and alias must match `application.properties`:

```powershell
New-Item -ItemType Directory -Force src\main\resources | Out-Null
keytool -genkeypair -alias bankledger -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore src\main\resources\keystore.p12 -validity 3650 -storepass changeit -keypass changeit -dname "CN=localhost"
mvn spring-boot:run
```

Open https://localhost:8443. A self-signed certificate causes a browser warning locally; that is expected for this development/assignment setup. The CSV ledger is stored at `./data/ledger.csv`, so the balance and complete transaction ledger survive application restarts. The undo stack is rebuilt from persisted transaction IDs on startup.

## Package for sharing

Build the standalone executable JAR:

```powershell
mvn clean package
Copy-Item target\bank-ledger-web-1.0.0.jar bank-ledger-web.jar
java -jar bank-ledger-web.jar
```

Anyone with JDK 17 or later can run `bank-ledger-web.jar`; Maven is not required. Open https://localhost:8443 after startup. The JAR includes the local development certificate and frontend. The `data` folder is created beside the JAR and stores the CSV ledger.

## Project structure

| File | Purpose |
| --- | --- |
| `pom.xml` | Maven build and Spring Boot dependencies. |
| `src/main/java/com/atomtraining/bankledger/BankLedgerApplication.java` | Spring Boot application entry point. |
| `src/main/java/com/atomtraining/bankledger/BankAccountController.java` | REST endpoints for all account operations. |
| `src/main/java/com/atomtraining/bankledger/BankAccountService.java` | Account rules, persistent ledger, and undo-stack reconstruction. |
| `src/main/java/com/atomtraining/bankledger/Transaction.java` | Domain transaction used by the ledger and custom stack. |
| `src/main/java/com/atomtraining/bankledger/TransactionStack.java` | Hand-written linked-list stack used for LIFO undo functionality. |
| `src/main/resources/static/` | Browser interface served by Spring Boot. |
| `data/ledger.csv` | CSV transaction ledger created at runtime. |

## How the transaction ledger works

`BankAccountService` maintains the history using:

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

The browser interface uses:

- A header that permanently displays the available balance.
- Tabs that switch between Deposit, Withdraw, Check Balance, Mini-Statement, and Undo screens.
- Inline green success messages and red validation/error messages.

## Validation handled

- Empty or non-numeric amounts
- Zero or negative amounts
- Insufficient balance for withdrawals
- Invalid mini-statement count
- Undo requested when no transaction exists
