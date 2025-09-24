package cc.jagind.jaguar.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "TRANSACTIONS")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long timestamp;

    private double amount;

    private String description;

    @ManyToOne
    @JoinColumn(name = "from_user_id")
    @JsonBackReference("user-sent-transactions")
    private User fromUser;

    @ManyToOne
    @JoinColumn(name = "to_user_id")
    @JsonBackReference("user-received-transactions")
    private User toUser;

    public Transaction() {
        this.timestamp = System.currentTimeMillis();
    }
}
