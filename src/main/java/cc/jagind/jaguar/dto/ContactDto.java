package cc.jagind.jaguar.dto;

import cc.jagind.jaguar.model.Contact;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactDto {

    private long id;
    private long ownerId;
    private long contactUserId;
    private long lastTransactionTimestamp;
    private String contactUserFullName;

    public ContactDto(Contact contact) {
        this.id = contact.getId();
        this.ownerId = contact.getOwner().getId();
        this.contactUserId = contact.getContactUser().getId();
        this.lastTransactionTimestamp = contact.getLastTransactionTimestamp();
        this.contactUserFullName = contact.getContactUser().getFullName();
    }
}