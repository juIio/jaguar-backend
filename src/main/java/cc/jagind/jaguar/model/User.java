package cc.jagind.jaguar.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String accountNumber;
    private String routingNumber;

    private String firstName;
    private String lastName;
    private String email;
    private String password;

    private double balance;

    private long verifiedAt;

    @OneToMany(mappedBy = "fromUser", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("user-sent-transactions")
    private List<Transaction> sentTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "toUser", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("user-received-transactions")
    private List<Transaction> receivedTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("user-contacts")
    private List<Contact> recentContacts = new ArrayList<>();

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public void addTransaction(Transaction transaction) {
        this.balance += transaction.getAmount();
        this.receivedTransactions.add(transaction);
    }
}
