package cc.jagind.jaguar.dto;

import cc.jagind.jaguar.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDashboardDto {
    private String firstName;
    private String lastName;
    private String accountNumber;
    private String routingNumber;
    private double balance;
    private List<TransactionDto> sentTransactions;
    private List<TransactionDto> receivedTransactions;

    public UserDashboardDto(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.accountNumber = user.getAccountNumber();
        this.routingNumber = user.getRoutingNumber();
        this.balance = user.getBalance();

        List<TransactionDto> sentTransactions = new ArrayList<>();
        user.getSentTransactions().forEach(transaction -> {
            sentTransactions.add(new TransactionDto(transaction));
        });
        this.sentTransactions = sentTransactions;

        List<TransactionDto> receivedTransactions = new ArrayList<>();
        user.getReceivedTransactions().forEach(transaction -> {
            receivedTransactions.add(new TransactionDto(transaction));
        });
        this.receivedTransactions = receivedTransactions;
    }
}
