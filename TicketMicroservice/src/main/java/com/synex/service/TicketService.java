package com.synex.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
        history.setActionDate(new Date());
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
        ticketRepo.save(ticket);
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

        ticket.setStatus("APPROVED");
        ticket.setAssignedTo(adminEmail);
        return ticketRepo.save(ticket);
    }

    // — New reject method —
    public Ticket rejectTicket(Long ticketId, String managerEmail) {
        Ticket ticket = ticketRepo.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (!"PENDING_APPROVAL".equals(ticket.getStatus()) ||
            !ticket.getAssignedTo().equals(managerEmail)) {
            throw new IllegalStateException("Cannot reject: not pending or not assigned to you.");
        }

        ticket.setStatus("REJECTED");
        ticket.setAssignedTo(ticket.getCreatedBy());
        return ticketRepo.save(ticket);
    }
    
    public Ticket resolveTicket(Long ticketId) {
        Ticket ticket = ticketRepo.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (!"APPROVED".equals(ticket.getStatus())) {
            throw new IllegalStateException("Only approved tickets can be resolved.");
        }

        ticket.setStatus("RESOLVED");
        ticket.setAssignedTo(ticket.getCreatedBy()); // Reassign to creator
        return ticketRepo.save(ticket);
    }
    
    public Ticket reopenTicket(Long ticketId) {
        Ticket ticket = ticketRepo.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));
        if (!"RESOLVED".equals(ticket.getStatus())) {
            throw new IllegalStateException("Only resolved tickets can be reopened.");
        }
        ticket.setStatus("REOPENED");
        return ticketRepo.save(ticket);
    }

    public Ticket closeTicket(Long ticketId) {
        Ticket ticket = ticketRepo.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));
        if (!"RESOLVED".equals(ticket.getStatus())) {
            throw new IllegalStateException("Only resolved tickets can be closed.");
        }
        ticket.setStatus("CLOSED");
        return ticketRepo.save(ticket);
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
        // Optional: status change logic here

        return ticketRepo.save(existing);
    }
}