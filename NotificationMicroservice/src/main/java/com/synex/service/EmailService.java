package com.synex.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

//    public void sendEmail(String toEmail, String subject, String body) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(toEmail);
//        message.setSubject(subject);
//        message.setText(body);
//        message.setFrom("your_email@gmail.com");  // Use your sender email
//
//        emailSender.send(message);
//    }
    
    public void sendTicketCreationEmail(String to, String subject, String body) {
    	System.out.println("Sending email to: " + to);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("your_email@gmail.com");
        emailSender.send(message);
    }
}