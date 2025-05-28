package com.synex.component;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
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
            JsonNode root = objectMapper.readTree(messageJson);
            String type = root.get("type").asText();

            switch (type) {
                case "plain":
                    EmailMessage plainEmail = objectMapper.treeToValue(root.get("email"), EmailMessage.class);
                    emailService.sendEmail(plainEmail.getTo(), plainEmail.getSubject(), plainEmail.getBody());
                    break;

                case "ticket_resolved":
                    Map<String, Object> ticket = objectMapper.convertValue(root.get("ticket"), new TypeReference<>() {});
                    emailService.sendResolvedTicketWithPdf(ticket);
                    break;

                default:
                    System.err.println("Unknown email type: " + type);
            }

        } catch (Exception e) {
            System.err.println("Failed to handle email message: " + e.getMessage());
        }
    }
}

