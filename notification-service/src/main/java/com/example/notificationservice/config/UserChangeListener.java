package com.example.notificationservice.config;

import com.example.notificationservice.DTO.Event;
import com.example.notificationservice.services.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class UserChangeListener {

    private final EmailService emailService;

    public UserChangeListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = "user-change-queue")
    public void handleUserChange(Event event) {
        emailService.sendUserChangeNotification(event);
    }
}