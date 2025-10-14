package cc.jagind.user.service;

import cc.jagind.commons.events.UserRegisteredEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate;

    @Value("${kafka.topic.user-registered}")
    private String userRegisteredTopic;

    public KafkaProducerService(KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishUserRegisteredEvent(String email, String fullName, String verificationCode) {
        UserRegisteredEvent event = new UserRegisteredEvent(email, fullName, verificationCode);
        kafkaTemplate.send(userRegisteredTopic, event);
    }
}

