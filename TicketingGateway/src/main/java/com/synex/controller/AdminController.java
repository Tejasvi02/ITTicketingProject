package com.synex.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ResponseBody;

import com.synex.component.TicketClient;

import java.util.List;
import java.util.Map;

@Controller
public class AdminController {

    @Autowired
    private TicketClient ticketClient;

    // Load JSP page
    @GetMapping("/admin/tickets")
    public String adminViewTicketsPage(Model model) {
        return "adminViewTickets"; // loads adminviewtickets.jsp
    }

    // Serve ticket data to AJAX
    @GetMapping("/admin/api/tickets")
    @ResponseBody
    public List<Map<String, Object>> getAllTickets() {
        List<Map<String, Object>> tickets = ticketClient.getAllTickets();
        //System.out.println("Tickets returned to frontend: " + tickets); // DEBUG LOG
        return tickets;
    }
}

