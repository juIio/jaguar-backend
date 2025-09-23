package cc.jagind.jaguar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserTransactionsPageDto {
    private List<ContactDto> recentContacts;
    private double balance;
}
