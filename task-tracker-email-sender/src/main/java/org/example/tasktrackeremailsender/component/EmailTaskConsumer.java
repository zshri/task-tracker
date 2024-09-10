package org.example.tasktrackeremailsender.component;

import lombok.RequiredArgsConstructor;
import org.example.tasktrackeremailsender.model.EmailTask;
import org.example.tasktrackeremailsender.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class EmailTaskConsumer {

    private final EmailService emailService;


    @KafkaListener(topics = "EMAIL_SENDING_TASKS", groupId = "email-sender-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void consume(EmailTask emailTask) {
        emailService.sendEmail(emailTask);
    }
}