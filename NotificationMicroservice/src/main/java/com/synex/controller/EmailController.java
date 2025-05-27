package com.synex.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.synex.service.EmailService;

@RestController
@RequestMapping("/notify")
public class EmailController {

    @Autowired
    private EmailService emailService;

//    @PostMapping("/ticketCreated")
//    public String notifyTicketCreated(@RequestBody Map<String, String> payload) {
//        String to = payload.get("to");
//        String subject = payload.get("subject");
//        String body = payload.get("body");
//
//        emailService.sendTicketCreationEmail(to, subject, body);
//        return "Email sent to " + to;
//    }
}