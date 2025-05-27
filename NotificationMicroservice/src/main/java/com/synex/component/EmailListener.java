package com.synex.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synex.pojo.EmailMessage;
import com.synex.service.EmailService;

@Component
public class EmailListener {

    @Autowired
    private EmailService emailService;
    
    @JmsListener(destination = "ticket.email.queue")
    public void handleEmail(String messageJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            EmailMessage email = objectMapper.readValue(messageJson, EmailMessage.class);

            System.out.println("Received email to: " + email.getTo());
            emailService.sendEmail(email.getTo(), email.getSubject(), email.getBody());

        } catch (Exception e) {
            System.err.println("Failed to parse or send email: " + e.getMessage());
        }
    }
}

