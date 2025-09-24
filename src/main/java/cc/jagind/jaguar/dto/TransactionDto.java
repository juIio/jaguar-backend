package cc.jagind.jaguar.dto;

import cc.jagind.jaguar.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private long id;
    private long timestamp;
    private double amount;
    private String description;
    private long fromUserId;
    private String fromUserName;
    private long toUserId;
    private String toUserName;

    public TransactionDto(Transaction transaction) {
        this.id = transaction.getId();
        this.timestamp = transaction.getTimestamp();
        this.amount = transaction.getAmount();
        this.description = transaction.getDescription();
        this.toUserId = transaction.getToUser().getId();
        this.toUserName = transaction.getToUser().getFullName();

        if (transaction.getFromUser() == null) {
            this.fromUserId = -1;
            this.fromUserName = "JAGUAR FINANCIAL";
        } else {
            this.fromUserId = transaction.getFromUser().getId();
            this.fromUserName = transaction.getFromUser().getFullName();
        }
    }
}
