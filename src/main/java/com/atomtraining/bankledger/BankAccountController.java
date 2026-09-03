package com.atomtraining.bankledger;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BankAccountController {
    private final BankAccountService service;

    public BankAccountController(BankAccountService service) {
        this.service = service;
    }

    @PostMapping("/deposit")
    public Transaction deposit(@RequestBody AmountRequest request) {
        return service.deposit(request.amount());
    }

    @PostMapping("/withdraw")
    public Transaction withdraw(@RequestBody AmountRequest request) {
        return service.withdraw(request.amount());
    }

    @GetMapping("/balance")
    public Map<String, Double> balance() {
        return Map.of("balance", service.getBalance());
    }

    @GetMapping("/statement")
    public List<Transaction> statement(@RequestParam(defaultValue = "5") int n) {
        return service.statement(n);
    }

    @PostMapping("/undo")
    public Transaction undo() {
        return service.undo();
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> handleValidation(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", exception.getMessage()));
    }

    public record AmountRequest(double amount) { }
}