package com.synex.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendTicketCreationEmail(String toEmail, String ticketTitle, String ticketId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Ticket Created: " + ticketTitle);
        message.setText("Your ticket with ID " + ticketId + " has been successfully created. We will get back to you soon.");
        message.setFrom("your_email@gmail.com");

        emailSender.send(message);
    }
}