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
		        ObjectMapper objectMapper = new ObjectMapper();
		        String json = objectMapper.writeValueAsString(email);
		        jmsTemplate.convertAndSend("ticket.email.queue", json);
		        System.out.println("Sending JMS email message: " + to);
		    } catch (Exception e) {
		        System.out.println("Failed to send email notification: " + e.getMessage());
		    }
		}
}
