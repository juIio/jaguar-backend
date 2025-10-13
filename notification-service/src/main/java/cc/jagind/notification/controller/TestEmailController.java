package cc.jagind.notification.controller;

import cc.jagind.notification.service.HtmlEmailService;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notification")
public class TestEmailController {

    private final HtmlEmailService htmlEmailService;

    public TestEmailController(HtmlEmailService htmlEmailService) {
        this.htmlEmailService = htmlEmailService;
    }

    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> signInUser(@RequestBody Map<String, String> credentials) {
        Map<String, Object> response = new HashMap<>();

        String email = credentials.get("email");
        String fullName = credentials.get("fullName");

        try {
            htmlEmailService.sendVerificationEmail(email, fullName, "https://localhost:8082/api/notification/success");
            response.put("success", true);

            return ResponseEntity.ok(response);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
