package cc.jagind.user.dto;

import cc.jagind.user.model.User;

public record UserDashboardPageDto(
        String firstName,
        String lastName,
        String accountNumber,
        String routingNumber,
        double balance
) {
    public UserDashboardPageDto(User user) {
        this(
                user.getFirstName(),
                user.getLastName(),
                user.getAccountNumber(),
                user.getRoutingNumber(),
                user.getBalance()
        );
    }
}
