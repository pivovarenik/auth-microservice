package com.example.notificationservice.services;

import com.example.notificationservice.DTO.Event;
import com.example.notificationservice.enums.Option;
import jakarta.mail.internet.InternetAddress;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@Slf4j
@AllArgsConstructor
public class EmailService {

    private JavaMailSender mailSender;

    public void sendUserChangeNotification(Event event) {
        String[] recipients = Arrays.stream(event.getAddressees().split(","))
                .map(String::trim)
                .filter(email -> !email.isEmpty())
                .filter(this::isValidEmail)
                .distinct()
                .toArray(String[]::new);
        if (recipients.length == 0) {
            log.warn("Нет валидных получателей для пользователя {} с email {}", event.getUsername(), event.getEmail());
            return;
        }
        log.info("Sending user change to {}", Arrays.toString(recipients));
        SimpleMailMessage message = getSimpleMailMessage(event, recipients);

        try {
            mailSender.send(message);
            log.info("Successfully sent!");
        } catch (Exception e) {
            log.error("Ошибка отправки письма", e);
        }
    }

    private static SimpleMailMessage getSimpleMailMessage(Event event, String[] recipients) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("egorpivovarcik1@gmail.com");
        message.setTo(recipients);

        String action = switch (event.getOption()) {
            case DELETED -> "Удален";
            case CHANGED -> "Изменен";
            default -> "Создан";
        };

        message.setSubject(action + " пользователь " + event.getUsername());
        message.setText(action + " пользователь c логином: " + event.getUsername() + "\n и почтой: " + event.getEmail());
        return message;
    }

    private boolean isValidEmail(String email) {
        try {
            new InternetAddress(email).validate();
            return true;
        } catch (Exception e) {
            log.warn("Недопустимый email: {} - {}", email, e.getMessage());
            return false;
        }
    }
}