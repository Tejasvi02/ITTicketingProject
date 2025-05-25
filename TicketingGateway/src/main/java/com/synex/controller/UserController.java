package com.synex.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.synex.component.TicketClient;
import com.synex.model.TicketForm;
import com.synex.model.TicketHistoryDTO;

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
        //System.out.println("Fetching tickets for: " + username); 
        return ticketClient.getTicketsByCreatedBy(username);
    }
    
    
    @PostMapping("/user/api/ticket/{id}/request-approval")
    @ResponseBody
    public ResponseEntity<?> requestApproval(@PathVariable Long id, Principal principal) {
        try {
            String userEmail = principal.getName(); // only send logged-in user

            // TicketClient handles fetching manager and calling microservice
            ticketClient.sendForApproval(id, userEmail);

            return ResponseEntity.ok(Map.of("message", "Sent for approval."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", "Failed to send for approval"));
        }
    }
    
    @PostMapping("/user/api/ticket/{id}/reopen")
    @ResponseBody
    public ResponseEntity<?> reopenTicket(@PathVariable Long id) {
        ticketClient.reopenTicket(id);
        return ResponseEntity.ok(Map.of("message", "Ticket reopened."));
    }

    @PostMapping("/user/api/ticket/{id}/close")
    @ResponseBody
    public ResponseEntity<?> closeTicket(@PathVariable Long id) {
        ticketClient.closeTicket(id);
        return ResponseEntity.ok(Map.of("message", "Ticket closed."));
    }

    @GetMapping("/user/ticket/{id}/edit")
    public String editTicketPage(@PathVariable Long id, Model model) {
        Map<String, Object> ticket = ticketClient.getTicketById(id); // use REST call
        Object filePathsObj = ticket.get("fileAttachmentPaths"); 
        if (filePathsObj != null) {
            ticket.put("fileNames", filePathsObj);
        } else {
            ticket.put("fileNames", new ArrayList<String>());
        }
        model.addAttribute("ticket", ticket);
        return "userUpdateTicket"; // JSP page
    }
    
    @PostMapping("/user/api/ticket/{id}/update")
    public ResponseEntity<?> updateTicket(@PathVariable Long id,
                                          @ModelAttribute TicketForm form,
                                          @RequestParam("files") List<MultipartFile> newFiles) {
        try {
            ticketClient.updateTicketWithFiles(id, form, newFiles);
            return ResponseEntity.ok().body(Map.of("message", "Updated"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", "Update failed", "details", e.getMessage()));
        }
    }
    
    
    @GetMapping("/user/api/ticket/{ticketId}/history")
    public ResponseEntity<List<Map<String,Object>>> getTicketHistory(@PathVariable Long ticketId) {
        try {
            List<Map<String,Object>> history = ticketClient.getTicketHistory(ticketId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }


}