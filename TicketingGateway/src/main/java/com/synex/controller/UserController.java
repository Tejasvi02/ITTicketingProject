package com.synex.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.synex.component.TicketClient;

@Controller
public class UserController {

    @Autowired
    private TicketClient ticketClient;

    @GetMapping("/user/tickets")
    public String userViewTicketsPage() {
        return "userViewTickets"; // your JSP
    }

    @GetMapping("/user/api/tickets")
    @ResponseBody
    public List<Map<String, Object>> getUserTickets(Principal principal) {
        String username = principal.getName();
        System.out.println("Fetching tickets for: " + username); 
        return ticketClient.getTicketsByCreatedBy(username);
    }
}