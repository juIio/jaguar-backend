package cc.jagind.jaguar.dto;

import java.util.List;

public record UserTransactionsPageDto(
        List<ContactDto> recentContacts,
        double balance
) {
}
