package com.synex.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.synex.domain.TicketHistory;

public interface TicketHistoryRepository extends JpaRepository<TicketHistory, Long> {
}
