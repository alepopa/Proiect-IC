package com.ticket.checker.tickets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.ticket.checker.ticketchecker.exceptions.ResourceNotFoundException;
import com.ticket.checker.ticketchecker.exceptions.TicketExistsException;
import com.ticket.checker.ticketchecker.exceptions.TicketValidationException;
import com.ticket.checker.ticketchecker.users.User;
import com.ticket.checker.ticketchecker.users.UserController;
import com.ticket.checker.ticketchecker.users.UserUtil;
/**
 * @author Daniel Incicau, daniel.incicau@busymachines.com
 * @since 21/03/2019
 */
@RestController
public class TicketController {

	private TicketRepository ticketRepository;
	
	@Autowired
	private UserUtil userUtil;
	
	
	@GetMapping(path="/tickets")
	public MappingJacksonValue getTickets(@RequestParam(value="type", required=false) String type, @RequestParam(value="value", required=false) String value, Pageable pageable) {
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
		return setTicketFilters(ticketList, true);
	}
	
	@GetMapping("/tickets/{ticketId}")
	public MappingJacksonValue getTicketById(@PathVariable String ticketId) {
		Optional<Ticket> optional = ticketRepository.findById(ticketId);
		if(!optional.isPresent()) {
			throw new ResourceNotFoundException("Ticket " + ticketId + " was not found!");
		}
		Ticket ticket = optional.get();
		MappingJacksonValue map = setTicketFilters(ticket, false);
		return map;
	}
	
	@PostMapping("/tickets")
	public ResponseEntity<MappingJacksonValue> createTicket(@RequestHeader("Authorization") String authorization, @Valid @RequestBody Ticket ticket) {
		User soldBy = userUtil.getUserFromAuthorization(authorization);
		
		String ticketId = ticket.getTicketId();
		Optional<Ticket> optional = ticketRepository.findById(ticketId);
		if(optional.isPresent()) {
			throw new TicketExistsException("Ticket " + ticketId + " already exists!");
		}
		ticket.setSoldAt(new Date());
		ticket.setSoldBy(soldBy);

		ticketRepository.save(ticket);
		userUtil.incrementUserSoldTickets(soldBy);
		
		return new ResponseEntity<MappingJacksonValue>(setTicketFilters(ticket, true),HttpStatus.CREATED);
	}
	
	@PostMapping(path="/tickets/{ticketId}")
	public MappingJacksonValue editTicket(@PathVariable String ticketId, @Valid @RequestBody Ticket ticket) {
		Optional<Ticket> optional = ticketRepository.findById(ticketId);
		if(!optional.isPresent()) {
			throw new ResourceNotFoundException("Ticket " + ticketId + " was not found!");
		}
		
		Ticket existingTicket = optional.get();
		if(ticket.getSoldTo() != null) {
			existingTicket.setSoldTo(ticket.getSoldTo());
		}
		if(ticket.getSoldToBirthdate() != null) {
			existingTicket.setSoldToBirthdate(ticket.getSoldToBirthdate());
		}
		ticketRepository.save(existingTicket);
		
		return setTicketFilters(existingTicket, false);
	}
	
	@PostMapping(path="/tickets/validate/{ticketId}") 
	public MappingJacksonValue validateTicketById(@RequestHeader("Authorization") String authorization, @RequestHeader("validate") Boolean validate, @PathVariable String ticketId) {
		User userMakingRequest = userUtil.getUserFromAuthorization(authorization);
		
		Optional<Ticket> optional = ticketRepository.findById(ticketId);
		if(!optional.isPresent()) {
			throw new ResourceNotFoundException("Ticket " + ticketId + " was not found!");
		}
		Ticket ticket = optional.get();
		if(validate) {
			if(ticket.getValidatedAt() != null) {
				throw new TicketValidationException("Ticket "+ ticketId +" was already validated!");
			}
			userUtil.incrementUserValidatedTickets(userMakingRequest);
			ticket.setValidatedBy(userMakingRequest);
			ticket.setValidatedAt(new Date());
		}
		else {
			if(ticket.getValidatedAt() == null) {
				throw new TicketValidationException("Ticket "+ ticketId +" is not validated!");
			}
			User userThatValidatedTicket = ticket.getValidatedBy();
			userUtil.decrementUserValidatedTickets(userThatValidatedTicket);
			ticket.setValidatedBy(null);
			ticket.setValidatedAt(null);
		}
		ticketRepository.save(ticket);
		return setTicketFilters(ticket, false);
	}
	
	@DeleteMapping(path="/tickets/{ticketId}")
	public void deleteTicketById(@PathVariable String ticketId) {
		Optional<Ticket> optional = ticketRepository.findById(ticketId);
		if(!optional.isPresent()) {
			throw new ResourceNotFoundException("Ticket " + ticketId + " doesnt not exist!");
		}
		Ticket ticket = optional.get();
		User userThatSoldTheTicket = ticket.getSoldBy();
		userUtil.decrementUserSoldTickets(userThatSoldTheTicket);
		User userThatValidatedTheTicket = ticket.getValidatedBy();
		userUtil.decrementUserValidatedTickets(userThatValidatedTheTicket);
		ticketRepository.delete(ticket);
	}
	
	public static MappingJacksonValue setTicketFilters(Object ticketObject, boolean hideUserDetails) {
		MappingJacksonValue mapping = new MappingJacksonValue(ticketObject);
		SimpleFilterProvider ticketFilter = new SimpleFilterProvider().addFilter("TicketFilter", getTicketFilterProperty(hideUserDetails));
		if(!hideUserDetails) {
			ticketFilter.addFilter("UserFilter", UserController.getUserFilterProperty());
		}
		mapping.setFilters(ticketFilter);
		return mapping;
	}
	
	public static SimpleBeanPropertyFilter getTicketFilterProperty(boolean hideUserDetails) {
		if(hideUserDetails) {
			return SimpleBeanPropertyFilter.filterOutAllExcept("ticketId","soldTo","soldToBirthdate","telephone","soldAt","validatedAt");
		}
		else {
			return SimpleBeanPropertyFilter.serializeAll();
		}
}

}
