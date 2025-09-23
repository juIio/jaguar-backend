package cc.jagind.jaguar.controllers;

import cc.jagind.jaguar.model.Transaction;
import cc.jagind.jaguar.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createTransaction(@RequestBody Map<String, Object> transactionData) {
        Map<String, Object> response = new HashMap<>();

        try {
            long fromUserId = ((Number) transactionData.get("fromUserId")).longValue();
            long toUserId = ((Number) transactionData.get("toUserId")).longValue();
            double amount = ((Number) transactionData.get("amount")).doubleValue();

            Transaction transaction = transactionService.createTransaction(fromUserId, toUserId, amount);
            // TODO: Check if we need to send a TransactionDto (might not be necessary here?)

            response.put("success", true);
            response.put("message", "Transaction created successfully");
            response.put("transaction", transaction);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to create transaction: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTransactions() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Transaction> transactions = transactionService.getAllTransactions();
            response.put("success", true);
            response.put("transactions", transactions);
            response.put("count", transactions.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to retrieve transactions: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTransactionById(@PathVariable long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            Transaction transaction = transactionService.getTransactionById(id);
            response.put("success", true);
            response.put("transaction", transaction);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getTransactionsByUser(@PathVariable long userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Transaction> transactions = transactionService.getTransactionsByUser(userId);
            response.put("success", true);
            response.put("transactions", transactions);
            response.put("count", transactions.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/user/{userId}/sent")
    public ResponseEntity<Map<String, Object>> getTransactionsSentByUser(@PathVariable long userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Transaction> transactions = transactionService.getTransactionsSentByUser(userId);
            response.put("success", true);
            response.put("transactions", transactions);
            response.put("count", transactions.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/user/{userId}/received")
    public ResponseEntity<Map<String, Object>> getTransactionsReceivedByUser(@PathVariable long userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Transaction> transactions = transactionService.getTransactionsReceivedByUser(userId);
            response.put("success", true);
            response.put("transactions", transactions);
            response.put("count", transactions.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/between")
    public ResponseEntity<Map<String, Object>> getTransactionsBetweenUsers(
            @RequestParam long userId1,
            @RequestParam long userId2) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Transaction> transactions = transactionService.getTransactionsBetweenUsers(userId1, userId2);
            response.put("success", true);
            response.put("transactions", transactions);
            response.put("count", transactions.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/recent")
    public ResponseEntity<Map<String, Object>> getRecentTransactions() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Transaction> transactions = transactionService.getRecentTransactions();
            response.put("success", true);
            response.put("transactions", transactions);
            response.put("count", transactions.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to retrieve recent transactions: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/amount-range")
    public ResponseEntity<Map<String, Object>> getTransactionsByAmountRange(
            @RequestParam double minAmount,
            @RequestParam double maxAmount) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Transaction> transactions = transactionService.getTransactionsByAmountRange(minAmount, maxAmount);
            response.put("success", true);
            response.put("transactions", transactions);
            response.put("count", transactions.size());
            response.put("minAmount", minAmount);
            response.put("maxAmount", maxAmount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTransaction(@PathVariable long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            transactionService.deleteTransaction(id);
            response.put("success", true);
            response.put("message", "Transaction deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
