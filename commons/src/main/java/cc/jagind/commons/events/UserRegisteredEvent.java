package cc.jagind.commons.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisteredEvent {
    private String email;
    private String fullName;
    private String verificationCode;
}

