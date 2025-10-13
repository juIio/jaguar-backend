package cc.jagind.notification.service;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class HtmlEmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public HtmlEmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendVerificationEmail(String to, String fullName, String verificationLink) throws MessagingException {
        Context context = new Context();
        context.setVariable("fullName", fullName);
        context.setVariable("verificationLink", verificationLink);

        String htmlContent = templateEngine.process("verification-email.html", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject("Your JAGUAR Verification Link");
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}


