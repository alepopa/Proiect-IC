package com.ticket.checker.tickets;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Daniel Incicau, daniel.incicau@busymachines.com
 * @since 21/03/2019
 */

public interface TicketRepository extends JpaRepository<Ticket, String>{
	Page<Ticket> findAllByOrderBySoldAtDesc(Pageable pageable);
	Page<Ticket> findByValidatedAtIsNullOrderBySoldAtDesc(Pageable pageable);
	Page<Ticket> findByTicketIdStartsWithIgnoreCaseOrSoldToStartsWithIgnoreCase(String ticketId, String soldTo, Pageable pageable);
	Long countByValidatedAtIsNull();
	Page<Ticket> findByValidatedAtIsNotNullOrderByValidatedAtDesc(Pageable pageable);
	Long countByValidatedAtIsNotNull();
	Long countByTicketIdStartsWithIgnoreCaseOrSoldToStartsWithIgnoreCase(String ticketId, String soldTo);
	Long countBySoldAtBetween(Date startDate, Date endDate);
	Long countByValidatedAtBetween(Date startDate, Date endDate);
}