package edu.nitw.skillswap.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username:}") private String from;

    public MailService(JavaMailSender mailSender) { this.mailSender = mailSender; }

    public boolean isConfigured() { return from != null && !from.isBlank(); }

    public void send(String to, String subject, String body) {
        if (!isConfigured()) throw new IllegalStateException("Email is not configured");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
