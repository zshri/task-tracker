package org.example.tasktrackeremailsender.service;

import lombok.RequiredArgsConstructor;
import org.example.tasktrackeremailsender.model.EmailTask;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(EmailTask emailTask) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailTask.getTo());
        message.setSubject(emailTask.getSubject());
        message.setText(emailTask.getBody());
        mailSender.send(message);
    }
}
