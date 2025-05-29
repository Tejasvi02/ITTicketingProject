package com.synex.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.synex.domain.Ticket;
import com.synex.domain.TicketHistory;
import com.synex.repository.TicketHistoryRepository;
import com.synex.repository.TicketRepository;

@Service
public class TicketService {

    @Autowired
    TicketRepository ticketRepo;

    @Autowired
    TicketHistoryRepository historyRepo;
    
    

    public Ticket createTicket(Ticket ticket) {
        ticket.setCreationDate(new Date());
        ticket.setStatus("OPEN");

        Ticket saved = ticketRepo.save(ticket);

        TicketHistory history = new TicketHistory();
        history.setTicket(saved);
        history.setAction("CREATED");
        history.setActionDate(LocalDateTime.now());
        history.setActionBy(ticket.getCreatedBy());
        history.setComments("Ticket created");
        historyRepo.save(history);

        return saved;
    }

    public List<Ticket> getAllTickets() {
        return ticketRepo.findAll();
    }
    
    public List<Ticket> getTicketsByCreatedBy(String createdBy) {
        System.out.println("Fetching from DB for createdBy: " + createdBy); // <-- LOG HERE
        List<Ticket> tickets = ticketRepo.findByCreatedBy(createdBy);
        System.out.println("Tickets fetched: " + tickets); // <-- LOG HERE
        return tickets;
    }
    
    public void sendForApproval(Long ticketId, String managerEmail) {
        Ticket ticket = ticketRepo.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));

        ticket.setStatus("PENDING_APPROVAL");
        ticket.setAssignedTo(managerEmail);
        Ticket updated = ticketRepo.save(ticket);

        logHistory(updated, "SENT_FOR_APPROVAL", "Ticket sent for approval to manager: " + managerEmail,ticket.getCreatedBy(), LocalDateTime.now());
    }
    
    public List<Ticket> getTicketsAssignedToManager(String managerEmail) {
        return ticketRepo.findByAssignedToAndStatus(managerEmail, "PENDING_APPROVAL");
    }

    public Ticket approveTicket(Long ticketId, String adminEmail) {
        Ticket ticket = ticketRepo.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (!"PENDING_APPROVAL".equals(ticket.getStatus())) {
            throw new IllegalStateException("Ticket is not in pending state.");
        }
        String manageremail = ticket.getAssignedTo();
        ticket.setStatus("APPROVED");
        ticket.setAssignedTo(adminEmail);
        Ticket updated = ticketRepo.save(ticket);

        logHistory(updated, "APPROVED", "Ticket approved and assigned to admin: " + adminEmail,manageremail, LocalDateTime.now());
        return updated;
    }


    public Ticket rejectTicket(Long ticketId, String managerEmail, String reason) {
        Ticket ticket = ticketRepo.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (!"PENDING_APPROVAL".equals(ticket.getStatus()) ||
            !ticket.getAssignedTo().equals(managerEmail)) {
            throw new IllegalStateException("Cannot reject: not pending or not assigned to you.");
        }

        ticket.setStatus("REJECTED");
        ticket.setAssignedTo(ticket.getCreatedBy());
        Ticket updated = ticketRepo.save(ticket);

        String fullComment = "Ticket rejected by manager: " + managerEmail + 
                             (reason != null && !reason.isBlank() ? " - Reason: " + reason : "");

        logHistory(updated, "REJECTED", fullComment, managerEmail, LocalDateTime.now());

        return updated;
    }

    public Ticket resolveTicket(Long ticketId, String comment) {
        Ticket ticket = ticketRepo.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (!"APPROVED".equals(ticket.getStatus())) {
            throw new IllegalStateException("Only approved tickets can be resolved.");
        }

        ticket.setStatus("RESOLVED");
        ticket.setAssignedTo(ticket.getCreatedBy());
        Ticket updated = ticketRepo.save(ticket);

        String fullComment = "Resolved by Admin - Resolution comments: " + comment;
        logHistory(updated, "RESOLVED", fullComment, "admin@gmail.com", LocalDateTime.now());

        return updated;
    }

    
    
    public Ticket reopenTicket(Long ticketId) {
        Ticket ticket = ticketRepo.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));
        if (!"RESOLVED".equals(ticket.getStatus())) {
            throw new IllegalStateException("Only resolved tickets can be reopened.");
        }
        ticket.setStatus("REOPENED");
        Ticket updated = ticketRepo.save(ticket);

        logHistory(updated, "REOPENED", "Ticket reopened by user",ticket.getCreatedBy(), LocalDateTime.now());
        return updated;
    }

    public Ticket closeTicket(Long ticketId) {
        Ticket ticket = ticketRepo.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));
        if (!"RESOLVED".equals(ticket.getStatus())) {
            throw new IllegalStateException("Only resolved tickets can be closed.");
        }
        ticket.setStatus("CLOSED");
        Ticket updated = ticketRepo.save(ticket);

        logHistory(updated, "CLOSED", "Ticket closed by user",ticket.getCreatedBy(), LocalDateTime.now());
        return updated;
    }


    public Ticket getTicketById(Long id) {
        return ticketRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));
    }


    public Ticket save(Ticket ticket) {
        return ticketRepo.save(ticket);
    }

    public Ticket updateTicket(Long id, Ticket updatedData) {
        Ticket existing = ticketRepo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));

        existing.setDescription(updatedData.getDescription());
        existing.setPriority(updatedData.getPriority());
        existing.setCategory(updatedData.getCategory());
        existing.setFileAttachmentPaths(updatedData.getFileAttachmentPaths());

        Ticket updated = ticketRepo.save(existing);
        logHistory(updated, "UPDATED", "Ticket fields updated",updated.getCreatedBy(), LocalDateTime.now());
        return updated;
    }
    
    //ticket history
    private void logHistory(Ticket ticket, String action, String comments, String actionBy, LocalDateTime actionDate) {
        TicketHistory history = new TicketHistory();
        history.setTicket(ticket);
        history.setAction(action);
        history.setComments(comments);
        history.setActionDate(actionDate);
        history.setActionBy(actionBy);
        historyRepo.save(history);
    }
    
    public List<TicketHistory> getHistoryByTicketId(Long ticketId) {
        return historyRepo.findByTicketId(ticketId);
    }
    public List<Ticket> getTicketsAssignedToWithStatus(String email, String status) {
        return ticketRepo.findByAssignedToAndStatus(email, status);
    }
    
    public List<Ticket> getTicketsAssignedTo(String email) {
        return ticketRepo.findByAssignedTo(email);
    }
    
//    public Ticket autoCloseTicket(Ticket ticket) {
//        if (!"RESOLVED".equals(ticket.getStatus())) {
//            throw new IllegalStateException("Only resolved tickets can be auto-closed.");
//        }
//
//        ticket.setStatus("CLOSED");
//        Ticket updated = ticketRepo.save(ticket);
//        System.out.println("Updated ticket ID " + updated.getId() + " to status: " + updated.getStatus());
//
//        logHistory(updated, "CLOSED", "Ticket auto-closed after 5 days of inactivity", "system@autoclose", LocalDateTime.now());
//        return updated;
//    }
    
    public Ticket autoCloseTicket(Ticket ticket) {
        if (!"RESOLVED".equals(ticket.getStatus())) {
            System.out.println("Ticket not in RESOLVED state: " + ticket.getId());
            return ticket;
        }

        ticket.setStatus("CLOSED");
        Ticket updated = ticketRepo.saveAndFlush(ticket); // Immediate DB write

        logHistory(updated, "CLOSED", "Ticket auto-closed after 5 days of inactivity", "system@autoclose", LocalDateTime.now());
        return updated;
    }




}