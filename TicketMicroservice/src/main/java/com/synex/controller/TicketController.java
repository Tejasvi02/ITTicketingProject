package com.synex.controller;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
        return ticketService.createTicket(ticket);
    }
    //without file upload
//    @PostMapping("/tickets")
//    public ResponseEntity<Ticket> createTicket(@RequestBody Ticket ticket) {
//        Ticket createdTicket = ticketService.createTicket(ticket);
//        return new ResponseEntity<>(createdTicket, HttpStatus.CREATED);
//    }
    
//    @PostMapping("/ticket/{id}/request-approval")
//    public ResponseEntity<?> requestApproval(@PathVariable Long id, @RequestParam String managerEmail) {
//        ticketService.sendForApproval(id, managerEmail);
//        return ResponseEntity.ok().build();
//    }

    @PostMapping("/ticket/{id}/request-approval")
    public ResponseEntity<?> requestApproval(
            @PathVariable Long id,
            @RequestParam String managerEmail) {

        // 1) Decode URL‑encoded data
        String decoded = URLDecoder.decode(managerEmail, StandardCharsets.UTF_8);

        // 2) Trim off any stray quotes at start/end
        if (decoded.startsWith("\"") && decoded.endsWith("\"")) {
            decoded = decoded.substring(1, decoded.length() - 1);
        }

        // 3) Log what we’re about to save
        System.out.println(">>> requestApproval raw managerEmail: `" + managerEmail + "`");
        System.out.println(">>> requestApproval cleaned decoded: `" + decoded + "`");
        System.out.println(">>> Length: " + decoded.length());

        ticketService.sendForApproval(id, decoded);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/ticket/{id}/reject")
    public ResponseEntity<?> rejectTicket(
        @PathVariable Long id,
        @RequestParam String managerEmail
    ) {
        ticketService.rejectTicket(id, managerEmail);
        return ResponseEntity.ok(Map.of("message","Ticket rejected."));
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
        ticketService.resolveTicket(id, comment);
        return ResponseEntity.ok(Map.of("message", "Ticket resolved successfully."));
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
    public ResponseEntity<?> approveTicket(
        @PathVariable Long id,
        @RequestParam String adminEmail    // injected by Gateway
    ) {
        ticketService.approveTicket(id, adminEmail);
        return ResponseEntity.ok(Map.of("message","Ticket approved."));
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
