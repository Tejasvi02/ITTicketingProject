package com.synex.component;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synex.model.EmailMessage;

@Component
public class NotificationClient {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendTicketCreationEmail(String to, String subject, String body) {
        try {
            EmailMessage email = new EmailMessage(to, subject, body);
            Map<String, Object> message = new HashMap<>();
            message.put("type", "plain");    // <-- Add this "type" wrapper
            message.put("email", email);

            String json = new ObjectMapper().writeValueAsString(message);
            jmsTemplate.convertAndSend("ticket.email.queue", json);
            System.out.println("Sending JMS email message: " + to);
        } catch (Exception e) {
            System.out.println("Failed to send email notification: " + e.getMessage());
        }
    }

    public void sendResolvedTicketNotification(Map<String, Object> ticketData) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("type", "ticket_resolved");
            message.put("ticket", ticketData);

            String json = new ObjectMapper().writeValueAsString(message);
            jmsTemplate.convertAndSend("ticket.email.queue", json);
            System.out.println("Sent ticket resolved data to notification service");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}