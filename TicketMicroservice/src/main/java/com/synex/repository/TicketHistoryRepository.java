package com.synex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.synex.domain.TicketHistory;

public interface TicketHistoryRepository extends JpaRepository<TicketHistory, Long> {
	List<TicketHistory> findByTicketId(Long ticketId);

}
