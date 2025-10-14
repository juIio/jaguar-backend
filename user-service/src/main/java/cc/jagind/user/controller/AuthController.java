package cc.jagind.user.controller;

import cc.jagind.commons.utils.BankNumberUtil;
import cc.jagind.commons.utils.JwtUtil;
import cc.jagind.commons.utils.NumberUtil;
import cc.jagind.grpc.TransactionProto;
import cc.jagind.grpc.TransactionServiceGrpc;
import cc.jagind.user.model.User;
import cc.jagind.user.service.KafkaProducerService;
import cc.jagind.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
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
    private final KafkaProducerService kafkaProducerService;

    @Value("${frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public AuthController(UserService userService, JwtUtil jwtUtil,
                          TransactionServiceGrpc.TransactionServiceBlockingStub transactionStub,
                          KafkaProducerService kafkaProducerService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.transactionStub = transactionStub;
        this.kafkaProducerService = kafkaProducerService;
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

            // user.setVerifiedAt(System.currentTimeMillis());
            user.setAccountNumber(BankNumberUtil.generateAccountNumber());
            user.setRoutingNumber(BankNumberUtil.generateRoutingNumber());
            user.setBalance(24999.99);
            user.setVerificationCode(NumberUtil.generateVerificationCode());

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

            String fullName = user.getFirstName() + " " + user.getLastName();
            kafkaProducerService.publishUserRegisteredEvent(user.getEmail(), fullName, user.getVerificationCode());

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

    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyUser(@RequestParam("email") String email, @RequestParam("code") String code) {
        Map<String, Object> response = new HashMap<>();

        User user = userService.getUserByVerificationCode(code);

        if (user == null) {
            response.put("success", false);
            response.put("message", "Not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        if (user.getVerifiedAt() != 0) {
            response.put("success", false);
            response.put("message", "Already verified");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        if (!user.getEmail().equals(email)) {
            response.put("success", false);
            response.put("message", "Invalid email");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        user.setVerifiedAt(System.currentTimeMillis());
        userService.saveUser(user);

        response.put("success", true);
        response.put("redirect", frontendUrl + "/signin");

        return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT).body(response);
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

