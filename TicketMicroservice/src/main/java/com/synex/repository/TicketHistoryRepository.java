package com.synex.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.synex.domain.TicketHistory;

public interface TicketHistoryRepository extends JpaRepository<TicketHistory, Long> {
	List<TicketHistory> findByTicketId(Long ticketId);
	
	@Query("SELECT th FROM TicketHistory th WHERE th.action = 'RESOLVED' AND th.actionDate <= :cutoffDate")
	List<TicketHistory> findResolvedTicketsBefore(@Param("cutoffDate") LocalDateTime cutoffDate);

}
