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
    
 
    
//Without file upload
//    public Ticket createTicket(Ticket ticket) {
//        ticket.setCreationDate(new Date());
//        ticket.setStatus("OPEN");
//
//        Ticket saved = ticketRepo.save(ticket);
//
//        TicketHistory history = new TicketHistory();
//        history.setTicket(saved);
//        history.setAction("CREATED");
//        history.setActionDate(new Date());
//        history.setComments("Ticket created");
//
//        historyRepo.save(history);
//        return saved;
//    }

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
    
//    public List<Ticket> getTicketsByCreator(String createdBy) {
//        return ticketRepo.findByCreatedBy(createdBy);
//    }
//    public Ticket resolveTicket(Long ticketId, String adminId) {
//        Ticket ticket = ticketRepo.findById(ticketId).orElseThrow(() -> new RuntimeException("Ticket not found"));
//        ticket.setStatus("RESOLVED");
//        ticket.setAssignedTo(adminId); // tracking which admin resolved
//        return ticketRepo.save(ticket);
//    }
//    
//    public Ticket reopenTicket(Long ticketId) {
//        Ticket ticket = ticketRepo.findById(ticketId).orElseThrow(() -> new RuntimeException("Ticket not found"));
//        ticket.setStatus("OPEN");
//        return ticketRepo.save(ticket);
//    }
}