package com.ticket.checker.tickets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.ticket.checker.exceptions.InvalidInputException;
import com.ticket.checker.exceptions.NotFoundException;
import com.ticket.checker.users.User;
import com.ticket.checker.utils.AuthUtil;
import com.ticket.checker.utils.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Daniel Incicau, daniel.incicau@busymachines.com
 * @since 21/03/2019
 */
@RestController
public class TicketController {

	@Autowired
	private TicketRepository ticketRepository;
	
	@Autowired
	private AuthUtil authUtil;

	@Autowired
	private UserUtil userUtil;
	
	
	@GetMapping(path="/tickets")
	public List<Ticket> getTickets(@RequestParam(value="type", required=false) String type, @RequestParam(value="value", required=false) String value, Pageable pageable) {
		List<Ticket> ticketList = new ArrayList<Ticket>();
		if(type != null && value != null) {
			switch(type.toUpperCase()) {
				case "VALIDATED": {
					if(value.toUpperCase().equals("TRUE")) {
						ticketList = ticketRepository.findByValidatedAtIsNotNullOrderByValidatedAtDesc(pageable).getContent();
					}
					else {
						ticketList = ticketRepository.findByValidatedAtIsNullOrderBySoldAtDesc(pageable).getContent();
					}
					break;
				}
				case "SEARCH": {
					ticketList = ticketRepository.findByTicketIdStartsWithIgnoreCaseOrSoldToStartsWithIgnoreCase(value, value, pageable).getContent();
					break;
				}
			}
		}
		else {
			ticketList = ticketRepository.findAllByOrderBySoldAtDesc(pageable).getContent();
		}
		return ticketList;
	}
	
	@GetMapping("/tickets/{ticketId}")
	public Ticket getTicketById(@PathVariable String ticketId) {
		Optional<Ticket> optional = ticketRepository.findById(ticketId);
		if(!optional.isPresent()) {
			throw new NotFoundException("Ticket " + ticketId + " was not found!");
		}
		return optional.get();
	}
	
	@PostMapping("/tickets")
	public ResponseEntity<Ticket> createTicket(@RequestHeader("Authorization") String authorization, @Valid @RequestBody Ticket ticket) {
		User soldBy = authUtil.getUserFromAuthorization(authorization);
		
		String ticketId = ticket.getTicketId();
		Optional<Ticket> optional = ticketRepository.findById(ticketId);
		if(optional.isPresent()) {
			throw new InvalidInputException("Ticket " + ticketId + " already exists!");
		}
		ticket.setSoldAt(new Date());
		ticket.setSoldBy(soldBy);

		ticketRepository.save(ticket);
		userUtil.incrementUserSoldTickets(soldBy);
		
		return new ResponseEntity<Ticket>(ticket, HttpStatus.CREATED);
	}
	
	@PostMapping(path="/tickets/{ticketId}")
	public Ticket editTicket(@PathVariable String ticketId, @Valid @RequestBody Ticket ticket) {
		Optional<Ticket> optional = ticketRepository.findById(ticketId);
		if(!optional.isPresent()) {
			throw new NotFoundException("Ticket " + ticketId + " was not found!");
		}
		
		Ticket existingTicket = optional.get();
		if(ticket.getSoldTo() != null) {
			existingTicket.setSoldTo(ticket.getSoldTo());
		}
		ticketRepository.save(existingTicket);
		return existingTicket;
	}
	
	@PostMapping(path="/tickets/validate/{ticketId}") 
	public Ticket validateTicketById(@RequestHeader("Authorization") String authorization, @RequestHeader("validate") Boolean validate, @PathVariable String ticketId) {
		User userMakingRequest = authUtil.getUserFromAuthorization(authorization);
		
		Optional<Ticket> optional = ticketRepository.findById(ticketId);
		if(!optional.isPresent()) {
			throw new NotFoundException("Ticket " + ticketId + " was not found!");
		}
		Ticket ticket = optional.get();
		if(validate) {
			if(ticket.getValidatedAt() != null) {
				throw new InvalidInputException("Ticket "+ ticketId +" was already validated!");
			}
			userUtil.incrementUserValidatedTickets(userMakingRequest);
			ticket.setValidatedBy(userMakingRequest);
			ticket.setValidatedAt(new Date());
		}
		else {
			if(ticket.getValidatedAt() == null) {
				throw new InvalidInputException("Ticket "+ ticketId +" is not validated!");
			}
			User userThatValidatedTicket = ticket.getValidatedBy();
			userUtil.decrementUserValidatedTickets(userThatValidatedTicket);
			ticket.setValidatedBy(null);
			ticket.setValidatedAt(null);
		}
		ticketRepository.save(ticket);
		return ticket;
	}
	
	@DeleteMapping(path="/tickets/{ticketId}")
	public void deleteTicketById(@PathVariable String ticketId) {
		Optional<Ticket> optional = ticketRepository.findById(ticketId);
		if(!optional.isPresent()) {
			throw new NotFoundException("Ticket " + ticketId + " doesnt not exist!");
		}
		Ticket ticket = optional.get();
		User userThatSoldTheTicket = ticket.getSoldBy();
		userUtil.decrementUserSoldTickets(userThatSoldTheTicket);
		User userThatValidatedTheTicket = ticket.getValidatedBy();
		userUtil.decrementUserValidatedTickets(userThatValidatedTheTicket);
		ticketRepository.delete(ticket);
	}

}
