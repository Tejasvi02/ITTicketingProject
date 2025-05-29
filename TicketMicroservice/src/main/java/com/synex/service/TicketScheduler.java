package com.synex.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synex.domain.Ticket;
import com.synex.dto.EmailMessage;
import com.synex.repository.TicketRepository;

@Component
public class TicketScheduler {

    @Autowired
    private TicketRepository ticketRepo;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private JmsTemplate jmsTemplate;
    

    private static final Logger logger = LoggerFactory.getLogger(TicketScheduler.class);

    //@Scheduled(cron = "0 0 2 * * *") // Runs daily at 2 AM
    @Scheduled(cron = "0 * * * * ?")
    public void autoCloseOldResolvedTickets() {
        logger.info("Auto-close scheduler started at {}", LocalDateTime.now());

        //LocalDateTime threshold = LocalDateTime.now().minusDays(5).withNano(0);
        
        LocalDate threshold = LocalDate.now().minusDays(5);
        System.out.println("Auto-close threshold: " + threshold);
        List<Ticket> tickets = ticketRepo.findResolvedTicketsOlderThan(threshold);
        System.out.println("Found " + tickets.size() + " resolved tickets to auto-close.");
        tickets.forEach(t -> System.out.println(" - Ticket ID: " + t.getId()));
        //System.out.println("Found " + tickets.size() + " resolved tickets older than " + threshold);

        logger.info("Found {} tickets to auto-close", tickets.size());

        for (Ticket ticket : tickets) {
            try {
                ticketService.autoCloseTicket(ticket);
                sendAutoCloseEmail(ticket);
                logger.info("Auto-closed ticket ID {}", ticket.getId());
            } catch (Exception e) {
                logger.error("Failed to auto-close ticket ID {}: {}", ticket.getId(), e.getMessage());
            }
        }
        logger.info("Auto-close scheduler finished");
    }


    private void sendAutoCloseEmail(Ticket ticket) {
        EmailMessage email = new EmailMessage(
            ticket.getCreatedBy(),
            "Ticket #" + ticket.getId() + " Automatically Closed",
            "Your ticket has been automatically closed after 5 days without activity following resolution."
        );

        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "plain");
        payload.put("email", Map.of(
            "to", email.getTo(),
            "subject", email.getSubject(),
            "body", email.getBody()
        ));

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(payload);
            jmsTemplate.convertAndSend("ticket.email.queue", json);
        } catch (Exception e) {
            System.err.println("Failed to serialize email payload: " + e.getMessage());
        }
    }
}

