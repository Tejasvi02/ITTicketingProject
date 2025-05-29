package com.synex.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synex.domain.Ticket;
import com.synex.dto.EmailMessage;
import com.synex.repository.TicketHistoryRepository;
import com.synex.repository.TicketRepository;


@Component
@ConditionalOnProperty(name = "scheduler.enabled", havingValue = "true")
public class TicketScheduler {

    @Autowired
    private TicketRepository ticketRepo;

    @Autowired
    private TicketHistoryRepository historyRepo;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private JmsTemplate jmsTemplate;

    private boolean running = false;
    private static final Logger logger = LoggerFactory.getLogger(TicketScheduler.class);

    @Scheduled(cron = "0 * * * * ?") // Every minute (for testing); adjust to daily in production
    public synchronized void autoCloseOldResolvedTickets() {
        if (running) return;
        running = true;

        try {
            logger.info("Auto-close scheduler started at {}", LocalDateTime.now());
            LocalDate threshold = LocalDate.now().minusDays(5);

            List<Ticket> tickets = ticketRepo.findResolvedTicketsOlderThan(threshold);
            logger.info("Found {} tickets to evaluate for auto-close", tickets.size());

            for (Ticket ticket : tickets) {
                Long ticketId = ticket.getId();

                // ✅ Skip if already auto-closed
                if (historyRepo.existsByTicketIdAndAction(ticketId, "CLOSED")) {
                    logger.info("Ticket ID {} already has CLOSED history, skipping.", ticketId);
                    continue;
                }

                try {
                    Ticket updated = ticketService.autoCloseTicket(ticket);
                    sendAutoCloseEmail(updated);
                    logger.info("Auto-closed ticket ID {}", updated.getId());

                } catch (Exception e) {
                    logger.error("Failed to auto-close ticket ID {}: {}", ticketId, e.getMessage(), e);
                }
            }

        } finally {
            running = false;
            logger.info("Auto-close scheduler finished.");
        }
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
            logger.error("Failed to send auto-close email for ticket ID {}: {}", ticket.getId(), e.getMessage());
        }
    }
}
