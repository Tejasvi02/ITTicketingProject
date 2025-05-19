package com.synex.controller;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.synex.domain.Ticket;
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


//    // Resolve a Ticket (by Admin)
//    @PutMapping("/resolve/{ticketId}")
//    public ResponseEntity<Ticket> resolveTicket(
//            @PathVariable Long ticketId,
//            @RequestParam String adminId) {
//
//        Ticket resolvedTicket = ticketService.resolveTicket(ticketId, adminId);
//        return ResponseEntity.ok(resolvedTicket);
//    }
//
	
    
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


}
