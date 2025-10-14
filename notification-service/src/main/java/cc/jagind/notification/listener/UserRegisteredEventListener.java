package cc.jagind.notification.listener;

import cc.jagind.commons.events.UserRegisteredEvent;
import cc.jagind.notification.service.HtmlEmailService;
import jakarta.mail.MessagingException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserRegisteredEventListener {

    private final HtmlEmailService emailService;

    public UserRegisteredEventListener(HtmlEmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "${kafka.topic.user-registered}", groupId = "${spring.kafka.consumer.group-id}")
    public void onUserRegistered(UserRegisteredEvent event) {
        String email = event.getEmail();
        String fullName = event.getFullName();
        String code = event.getVerificationCode();

        try {
            // TODO: Replace with dynamic link so it's not hard-coded
            String verificationLink = "https://localhost:8081/api/user/verify?code=" + code + "&email=" + email;
            emailService.sendVerificationEmail(email, fullName, verificationLink);

            System.out.println("Successfully sent verification email to: " + event.getEmail());
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

