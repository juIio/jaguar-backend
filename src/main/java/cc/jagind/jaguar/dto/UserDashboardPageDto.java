package cc.jagind.jaguar.dto;

import cc.jagind.jaguar.model.User;

import java.util.List;

public record UserDashboardPageDto(
        String firstName,
        String lastName,
        String accountNumber,
        String routingNumber,
        double balance,
        List<TransactionDto> sentTransactions,
        List<TransactionDto> receivedTransactions
) {
    public UserDashboardPageDto(User user) {
        this(
                user.getFirstName(),
                user.getLastName(),
                user.getAccountNumber(),
                user.getRoutingNumber(),
                user.getBalance(),
                user.getSentTransactions().stream()
                        .map(TransactionDto::new)
                        .toList(),
                user.getReceivedTransactions().stream()
                        .map(TransactionDto::new)
                        .toList()
        );
    }
}
