package com.synex.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.synex.domain.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
	List<Ticket> findByCreatedBy(String createdBy);
	List<Ticket> findByAssignedToAndStatus(String assignedTo, String status);
	List<Ticket> findByAssignedTo(String email);
	
	@Query("SELECT t FROM Ticket t WHERE t.status = 'RESOLVED' AND EXISTS " +
		       "(SELECT h FROM TicketHistory h WHERE h.ticket = t AND h.action = 'RESOLVED' AND CAST(h.actionDate AS date) <= :threshold)")
		List<Ticket> findResolvedTicketsOlderThan(@Param("threshold") LocalDate threshold);


}