package org.example.tasktrackerscheduler.component;

import lombok.RequiredArgsConstructor;
import org.example.tasktrackerscheduler.model.EmailTask;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class EmailTaskProducer {

    private final KafkaTemplate<String, EmailTask> kafkaTemplate;

    public void sendEmailTask(EmailTask emailTask) {
        kafkaTemplate.send("EMAIL_SENDING_TASKS", emailTask);
    }
}

