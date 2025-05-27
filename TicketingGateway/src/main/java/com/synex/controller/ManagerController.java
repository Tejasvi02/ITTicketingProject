package com.synex.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import com.synex.component.TicketClient;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    private TicketClient ticketClient;

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/tickets")
    public String viewTicketsToApprove() {
        return "managerTickets";
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/api/tickets")
    @ResponseBody
    public List<Map<String, Object>> getTicketsToApprove(Principal principal) {
        return ticketClient.getTicketsToApprove(principal.getName());
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/api/ticket/{id}/approve")
    @ResponseBody
    public ResponseEntity<?> approveTicket(@PathVariable Long id) {
        ticketClient.approveTicket(id);
        return ResponseEntity.ok(Map.of("message", "Ticket approved."));
    }
    
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/api/ticket/{id}/reject")
    @ResponseBody
    public ResponseEntity<?> rejectTicket(@PathVariable Long id, 
            @RequestParam String reason,
            Principal principal) {
    	ticketClient.rejectTicket(id, principal.getName(), reason);
    	return ResponseEntity.ok(Map.of("message", "Ticket rejected."));
    }
}