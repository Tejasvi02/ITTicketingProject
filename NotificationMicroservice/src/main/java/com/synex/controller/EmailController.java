package com.synex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.synex.service.EmailService;

@RestController
@RequestMapping("/notify")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/ticketCreated")
    public String notifyTicketCreated(@RequestParam String email,
                                      @RequestParam String title,
                                      @RequestParam String id) {
        emailService.sendTicketCreationEmail(email, title, id);
        return "Email sent to " + email;
    }
}