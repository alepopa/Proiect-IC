package com.ticket.checker.tickets;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, String>{
	Page<Ticket> findAllByOrderBySoldAtDesc(Pageable pageable);
	Page<Ticket> findByValidatedAtIsNullOrderBySoldAtDesc(Pageable pageable);
	Page<Ticket> findByTicketIdStartsWithIgnoreCaseOrSoldToStartsWithIgnoreCase(String ticketId, String soldTo, Pageable pageable);
	Page<Ticket> findByValidatedAtIsNotNullOrderByValidatedAtDesc(Pageable pageable);
}