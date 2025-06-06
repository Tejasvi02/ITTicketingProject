package com.synex.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.synex.domain.Ticket;
import com.synex.domain.TicketHistory;
import com.synex.service.TicketService;

@RestController
@CrossOrigin(origins = "*")
public class TicketController {
	

    @Autowired
    TicketService ticketService;
    
    @PostMapping("/tickets")
    public Ticket createTicket(@RequestBody Ticket ticket) {
    	System.out.println("Received paths: " + ticket.getFileAttachmentPaths());
        return ticketService.createTicket(ticket);
    }
    
    @GetMapping("/ticket/assigned")
    public ResponseEntity<List<Ticket>> getTicketsAssignedToAdmin() {
        String adminEmail = "tejasvijava555@gmail.com"; // Your hardcoded admin email

        List<Ticket> tickets = ticketService.getTicketsAssignedTo(adminEmail);

        return ResponseEntity.ok(tickets);
    }
    
    @PostMapping("/ticket/{id}/request-approval")
    public ResponseEntity<?> requestApproval(
            @PathVariable Long id,
            @RequestParam String managerEmail) throws UnsupportedEncodingException {

        // Fix any spaces turned from '+'
        String fixedEmail = managerEmail.replace(" ", "+");

        // Then decode normally
        String decoded = URLDecoder.decode(fixedEmail, StandardCharsets.UTF_8);

        // Now pass to your service
        ticketService.sendForApproval(id, decoded);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/ticket/{id}/reject")
    public ResponseEntity<Ticket> rejectTicket(
        @PathVariable Long id,
        @RequestParam String managerEmail,
        @RequestParam String reason
    ) {
        Ticket ticket = ticketService.rejectTicket(id, managerEmail, reason);
        return ResponseEntity.ok(ticket);
    }

    
    @GetMapping("/createdby/{createdBy}")
    public List<Ticket> getTicketsByCreatedBy(@PathVariable String createdBy) {
    	// System.out.println("Received request for createdBy: " + createdBy);
        return ticketService.getTicketsByCreatedBy(createdBy);
    }
	    
	
    // Get All Tickets (for Admin/Manager roles)
    @GetMapping("/getAllTickets")
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTickets();
    }

 
    @PostMapping("/ticket/{id}/resolve")
    public ResponseEntity<?> resolveTicket(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String comment = body.get("comment");
        Ticket ticket = ticketService.resolveTicket(id, comment);
        return ResponseEntity.ok(convertTicketToMap(ticket));
    }

    private Map<String, Object> convertTicketToMap(Ticket ticket) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", ticket.getId());
        map.put("title", ticket.getTitle());
        map.put("description", ticket.getDescription());
        map.put("status", ticket.getStatus());
        map.put("createdBy", ticket.getCreatedBy());
        return map;
    }
    
    
  //Testing for gateway and microservice connection
  	@RequestMapping(value = "/testGet/{data}",method = RequestMethod.GET)
  	public String testGet(@PathVariable String data) {
  		return "Welcome I am Weather API"+data;
  	}
  	
  	@RequestMapping(value = "/testPostUser",method = RequestMethod.POST)
  	public JsonNode testPost(@RequestBody JsonNode node) {
  		((ObjectNode) node).put("age",40); //to add a property in response - cast node and add
  		System.out.println(node.get("data"));
  		return node;
  	}

  	@GetMapping("/api/manager/tickets")
  	public ResponseEntity<List<Ticket>> getTicketsToApprove(@RequestParam String managerEmail) {
  	    return ResponseEntity.ok(ticketService.getTicketsAssignedToManager(managerEmail));
  	}
  
  	@PostMapping("/ticket/{id}/approve")
  	public ResponseEntity<Ticket> approveTicket(
  	    @PathVariable Long id,
  	    @RequestParam String adminEmail // injected by Gateway
  	) {
  	    Ticket ticket = ticketService.approveTicket(id, adminEmail);
  	    return ResponseEntity.ok(ticket);
  	}

  
    @PostMapping("/ticket/{id}/reopen")
    public ResponseEntity<?> reopenTicket(@PathVariable Long id) {
        ticketService.reopenTicket(id);
        return ResponseEntity.ok(Map.of("message", "Ticket reopened."));
    }

    @PostMapping("/ticket/{id}/close")
    public ResponseEntity<?> closeTicket(@PathVariable Long id) {
        ticketService.closeTicket(id);
        return ResponseEntity.ok(Map.of("message", "Ticket closed."));
    }
    
    @GetMapping("/ticket/{id}")
    public ResponseEntity<Ticket> getTicket(@PathVariable Long id) {
        Ticket ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ticket);
    }

    @PutMapping("/ticket/{id}")
    public ResponseEntity<Ticket> updateTicket(@PathVariable Long id, @RequestBody Ticket ticket) {
        Ticket updated = ticketService.updateTicket(id, ticket);
        return ResponseEntity.ok(updated);
    }
    
    @GetMapping("/ticket/{id}/history")
    public ResponseEntity<List<TicketHistory>> getTicketHistory(@PathVariable Long id) {
        List<TicketHistory> history = ticketService.getHistoryByTicketId(id);
        history.sort((h1, h2) -> h2.getActionDate().compareTo(h1.getActionDate()));
        return ResponseEntity.ok(history);
    }



}
