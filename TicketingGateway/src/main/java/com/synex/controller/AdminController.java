package com.synex.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ResponseBody;

import com.synex.component.TicketClient;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    
    @PostMapping("/admin/api/ticket/{id}/resolve")
    @ResponseBody
    public ResponseEntity<?> resolveTicket(@PathVariable Long id) {
        ticketClient.resolveTicket(id);
        return ResponseEntity.ok(Map.of("message", "Ticket resolved."));
    }
    
 // Show JSP for assigned tickets
    @GetMapping("/admin/assigned-tickets")
    public String adminAssignedTicketsPage() {
        return "adminAssignedTickets"; // ✅ create adminAssignedTickets.jsp
    }

    // Serve only assigned tickets (for logged-in admin)
    @GetMapping("/admin/api/assigned-tickets")
    @ResponseBody
    public List<Map<String, Object>> getAssignedTickets(Principal principal) {
        String adminEmail = principal.getName(); // ✅ email injected from session

        // Filter tickets assigned to this admin
        return ticketClient.getAllTickets().stream()
            .filter(ticket -> adminEmail.equals(ticket.get("assignedTo")))
            .collect(Collectors.toList());
    }
}

