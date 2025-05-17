package com.synex.controller;

import java.io.IOException;
import java.util.List;

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

//
//	    @GetMapping("/getAllTickets")
//	    public List<Ticket> getAllTickets() {
//	        return ticketService.getAllTickets();
//	    }
//	    
    
    
    @GetMapping("/createdby/{createdBy}")
    public List<Ticket> getTicketsByCreatedBy(@PathVariable String createdBy) {
    	 System.out.println("Received request for createdBy: " + createdBy);
        return ticketService.getTicketsByCreatedBy(createdBy);
    }
	    
	
    // Get All Tickets (for Admin/Manager roles)
    @GetMapping("/getAllTickets")
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTickets();
    }

//    // Get Tickets by CreatedBy (String userId like "emp123")
//    @GetMapping("/tickets/user/{userId}")
//    public List<Ticket> getTicketsByUserId(@PathVariable String userId) {
//        return ticketService.getTicketsByCreator(userId);
//    }
//
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
//    // Reopen a Ticket (by Admin)
//    @PutMapping("/reopen/{ticketId}")
//    public ResponseEntity<Ticket> reopenTicket(@PathVariable Long ticketId) {
//        Ticket ticket = ticketService.reopenTicket(ticketId);
//        return ResponseEntity.ok(ticket);
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

}
