package cc.jagind.transaction.controller;

import cc.jagind.commons.utils.JwtUtil;
import cc.jagind.grpc.UserProto;
import cc.jagind.grpc.UserServiceGrpc;
import cc.jagind.transaction.dto.TransactionDto;
import cc.jagind.transaction.model.Transaction;
import cc.jagind.transaction.service.TransactionService;
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
    private final UserServiceGrpc.UserServiceBlockingStub userServiceStub;
    private final JwtUtil jwtUtil;

    public TransactionController(TransactionService transactionService, UserServiceGrpc.UserServiceBlockingStub userServiceStub, JwtUtil jwtUtil) {
        this.transactionService = transactionService;
        this.userServiceStub = userServiceStub;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createTransaction(@RequestHeader("Authorization") String authHeader, @RequestBody Map<String, Object> transactionData) {
        Map<String, Object> response = new HashMap<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        long fromUserId = jwtUtil.getUserIdFromToken(token);

        UserProto.EmailRequest request = UserProto.EmailRequest.newBuilder()
                .setEmail((String) transactionData.get("recipientEmail"))
                .build();

        UserProto.UserIdResponse emailResponse = userServiceStub.getUserIdByEmail(request);

        if (!emailResponse.getFound()) {
            response.put("success", false);
            response.put("message", "Failed to create transaction: Recipient not found");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        long recipientUserId = emailResponse.getUserId();

        try {
            double amount = ((Number) transactionData.get("amount")).doubleValue();
            String description = (String) transactionData.get("description");

            Transaction transaction = transactionService.createTransaction(fromUserId, recipientUserId, amount, description);
            
            String fromUserEmail = "";
            try {
                UserProto.UserIdRequest fromEmailRequest = UserProto.UserIdRequest.newBuilder()
                        .setUserId(fromUserId)
                        .build();
                UserProto.EmailResponse fromEmailResponse = userServiceStub.getEmailByUserId(fromEmailRequest);
                if (fromEmailResponse.getFound()) {
                    fromUserEmail = fromEmailResponse.getEmail();
                }
            } catch (Exception e) {
                System.err.println("Failed to retrieve email for userId: " + fromUserId);
            }
            
            TransactionDto transactionDto = new TransactionDto(transaction, fromUserEmail, (String) transactionData.get("recipientEmail"));

            response.put("success", true);
            response.put("message", "Transaction created successfully");
            response.put("transaction", transactionDto);
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

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getTransactionsByUser(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("success", false);
            response.put("message", "Missing or invalid Authorization header");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        String token = authHeader.substring(7);
        long userId = jwtUtil.getUserIdFromToken(token);

        try {
            List<Transaction> transactions = transactionService.getTransactionsToUser(userId);

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

    @DeleteMapping()
    public ResponseEntity<Map<String, Object>> deleteAll() {
        Map<String, Object> response = new HashMap<>();

        try {
            transactionService.deleteAllTransactions();
            response.put("success", true);
            response.put("message", "All Transactions deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}

