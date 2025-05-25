package com.synex.component;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class NotificationClient {

    private static final String NOTIFICATION_URL = "http://localhost:8484/notify/ticketCreated";

    public void sendTicketCreationEmail(String to, String subject, String body) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> mailRequest = new HashMap<>();
        mailRequest.put("to", to);
        mailRequest.put("subject", subject);
        mailRequest.put("body", body);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(mailRequest, headers);

        try {
            restTemplate.postForEntity(NOTIFICATION_URL, request, String.class);
            System.out.println("Email sent to: " + to);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
}
