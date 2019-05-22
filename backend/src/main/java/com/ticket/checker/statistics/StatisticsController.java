package com.ticket.checker.statistics;

import com.ticket.checker.tickets.TicketRepository;
import com.ticket.checker.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticsController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private TicketRepository ticketRepository;

	@GetMapping(path="/statistics/numbers/users")
	public Long getUsersCount(@RequestParam(value="type", required=false) String type, @RequestParam(value="value", required=false) String value) {
		Long usersNumbers = 0L;
		if(type != null && value != null) {
			switch(type.toUpperCase()) {
				case "ROLE": {
					usersNumbers = userRepository.countByRole("ROLE_" + value.toUpperCase());
					break;
				}
				case "SEARCH": {
					usersNumbers = userRepository.countByNameStartsWithIgnoreCase(value);
					break;
				}
			}
		}
		else {
			usersNumbers = userRepository.count();
		}
		return usersNumbers;
	}
	
	@GetMapping(path="/statistics/numbers/tickets") 
	public Long getTicketsCount(@RequestParam(value="type", required=false) String type, @RequestParam(value="value", required=false) String value) {
		Long ticketNumbers = 0L;
		if(type!=null && value != null) {
			switch(type.toUpperCase()) {
				case "VALIDATED": {
					if(value.toUpperCase().equals("TRUE")) {
						ticketNumbers = ticketRepository.countByValidatedAtIsNotNull();
					}
					else {
						ticketNumbers = ticketRepository.countByValidatedAtIsNull();
					}
					break;
				}
				case "SEARCH": {
					ticketNumbers = ticketRepository.countByTicketIdStartsWithIgnoreCaseOrSoldToStartsWithIgnoreCase(value, value);
					break;
				}
			}
		}
		else {
			ticketNumbers = ticketRepository.count();
		}
		return ticketNumbers;
	}
 }
