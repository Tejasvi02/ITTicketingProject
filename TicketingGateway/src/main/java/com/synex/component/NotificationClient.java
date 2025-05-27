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

   // private static final String NOTIFICATION_URL = "http://localhost:8484/notify/ticketCreated";

//    public void sendTicketCreationEmail(String to, String subject, String body) {
//        RestTemplate restTemplate = new RestTemplate();
//
//        Map<String, String> mailRequest = new HashMap<>();
//        mailRequest.put("to", to);
//        mailRequest.put("subject", subject);
//        mailRequest.put("body", body);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<Map<String, String>> request = new HttpEntity<>(mailRequest, headers);
//
//        try {
//            restTemplate.postForEntity(NOTIFICATION_URL, request, String.class);
//            System.out.println("Email sent to: " + to);
//        } catch (Exception e) {
//            System.err.println("Failed to send email: " + e.getMessage());
//        }
//    }
	 
	 public void sendTicketCreationEmail(String to, String subject, String body) {
		    try {
		        EmailMessage email = new EmailMessage(to, subject, body);
		        ObjectMapper objectMapper = new ObjectMapper();
		        String json = objectMapper.writeValueAsString(email);
		        jmsTemplate.convertAndSend("ticket.email.queue", json);
		        System.out.println("Sending JMS email message: " + to);
		    } catch (Exception e) {
		        System.out.println("Failed to send email notification: " + e.getMessage());
		    }
		}
}
