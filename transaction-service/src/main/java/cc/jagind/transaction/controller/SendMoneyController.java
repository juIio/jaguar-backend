package cc.jagind.transaction.controller;

import cc.jagind.commons.utils.JwtUtil;
import cc.jagind.grpc.UserProto;
import cc.jagind.grpc.UserServiceGrpc;
import cc.jagind.transaction.dto.TransactionDto;
import cc.jagind.transaction.model.Transaction;
import cc.jagind.transaction.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class SendMoneyController {

    private final TransactionService transactionService;
    private final UserServiceGrpc.UserServiceBlockingStub userServiceStub;
    private final JwtUtil jwtUtil;

    public SendMoneyController(TransactionService transactionService, UserServiceGrpc.UserServiceBlockingStub userServiceStub, JwtUtil jwtUtil) {
        this.transactionService = transactionService;
        this.userServiceStub = userServiceStub;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/sent")
    public ResponseEntity<Map<String, Object>> getTransactionsData(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        long userId = jwtUtil.getUserIdFromToken(token);

        List<TransactionDto> sentDtoList = new ArrayList<>();
        List<Transaction> sentTransactions = transactionService.getTransactionsFromUser(userId);
        for (Transaction transaction : sentTransactions) {
            String fromUserEmail = "";
            String toUserEmail = "";

            try {
                UserProto.UserIdRequest emailRequest = UserProto.UserIdRequest.newBuilder()
                        .setUserId(transaction.getFromUserId())
                        .build();
                UserProto.EmailResponse emailResponse = userServiceStub.getEmailByUserId(emailRequest);
                if (emailResponse.getFound()) {
                    fromUserEmail = emailResponse.getEmail();
                }
            } catch (Exception e) {
                System.err.println("Failed to retrieve email for userId: " + transaction.getFromUserId());
            }

            try {
                UserProto.UserIdRequest emailRequest = UserProto.UserIdRequest.newBuilder()
                        .setUserId(transaction.getToUserId())
                        .build();
                UserProto.EmailResponse emailResponse = userServiceStub.getEmailByUserId(emailRequest);
                if (emailResponse.getFound()) {
                    toUserEmail = emailResponse.getEmail();
                }
            } catch (Exception e) {
                System.err.println("Failed to retrieve email for userId: " + transaction.getToUserId());
            }

            sentDtoList.add(new TransactionDto(transaction, fromUserEmail, toUserEmail));
        }

        response.put("transactions", sentDtoList);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
