package cc.jagind.jaguar.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "CONTACTS")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "owner_user_id", nullable = false)
    @JsonBackReference("user-contacts")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "contact_user_id", nullable = false)
    private User contactUser;

    private long lastTransactionTimestamp;
}
