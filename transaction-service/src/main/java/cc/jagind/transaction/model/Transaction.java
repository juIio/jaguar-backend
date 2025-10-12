package cc.jagind.transaction.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long timestamp;

    private double amount;

    private String description;

    private long fromUserId;

    private long toUserId;

    private Transaction.TransactionResult result;

    public Transaction() {
        this.timestamp = System.currentTimeMillis();
    }

    public enum TransactionResult {
        APPROVED,
        DECLINED;
    }
}

