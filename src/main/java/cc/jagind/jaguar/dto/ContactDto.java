package cc.jagind.jaguar.dto;

import cc.jagind.jaguar.model.Contact;

public record ContactDto(
        long id,
        long ownerId,
        long contactUserId,
        long lastTransactionTimestamp,
        String contactFullName,
        String contactEmail
) {
    public ContactDto(Contact contact) {
        this(
                contact.getId(),
                contact.getOwner().getId(),
                contact.getContactUser().getId(),
                contact.getLastTransactionTimestamp(),
                contact.getContactUser().getFullName(),
                contact.getContactUser().getEmail()
        );
    }
}