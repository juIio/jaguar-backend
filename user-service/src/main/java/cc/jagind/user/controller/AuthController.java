package cc.jagind.user.controller;

import cc.jagind.commons.utils.BankNumberUtil;
import cc.jagind.commons.utils.JwtUtil;
import cc.jagind.grpc.TransactionProto;
import cc.jagind.grpc.TransactionServiceGrpc;
import cc.jagind.user.model.User;
import cc.jagind.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final TransactionServiceGrpc.TransactionServiceBlockingStub transactionStub;

    public AuthController(UserService userService, JwtUtil jwtUtil, TransactionServiceGrpc.TransactionServiceBlockingStub transactionStub) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.transactionStub = transactionStub;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (userService.getUserByEmail(user.getEmail()) != null) {
                response.put("success", false);
                response.put("message", "User with this email already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            user.setVerifiedAt(System.currentTimeMillis());
            user.setAccountNumber(BankNumberUtil.generateAccountNumber());
            user.setRoutingNumber(BankNumberUtil.generateRoutingNumber());
            user.setBalance(24999.99);

            userService.saveUser(user);

            TransactionProto.CreateTransactionRequest transactionRequest = TransactionProto.CreateTransactionRequest.newBuilder()
                    .setFromUserId(0)
                    .setToUserId(user.getId())
                    .setAmount(24999.99)
                    .setDescription("Early user deposit")
                    .build();

            TransactionProto.CreateTransactionResponse txnResponse = transactionStub.createTransaction(transactionRequest);

            if (txnResponse.getSuccess()) {
                System.out.println("Successfully created initial deposit for user with ID: " + user.getId());
            } else {
                response.put("success", false);
                response.put("message", "gRPC Response Error");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

            response.put("success", true);
            response.put("message", "Registration successful");
            response.put("token", jwtUtil.createTokenFromId(user.getId()));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<Map<String, Object>> signInUser(@RequestBody Map<String, String> credentials) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = credentials.get("email");
            String password = credentials.get("password");

            User user = userService.authenticateUser(email, password);

            if (user != null) {
                response.put("success", true);
                response.put("message", "Successful login");
                response.put("token", jwtUtil.createTokenFromId(user.getId()));

                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Invalid email or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Sign in failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

