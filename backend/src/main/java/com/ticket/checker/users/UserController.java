package com.ticket.checker.ticketchecker.users;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.ticket.checker.ticketchecker.exceptions.UnauthorizedRequestException;
import com.ticket.checker.ticketchecker.exceptions.UsernameExistsException;
import com.ticket.checker.ticketchecker.security.SpringSecurityConfig;
import com.ticket.checker.ticketchecker.tickets.Ticket;
import com.ticket.checker.ticketchecker.tickets.TicketController;

@RestController
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserUtil util;
	
	@Value("${ticket.checker.application.name:Ticket Checker}")
	private String appName;

	@Value("${ticket.checker.admin.username:admin}")
	private String adminUsername;

	@Value("${ticket.checker.admin.password:admin}")
	private String adminPassword;

	@PostConstruct
	public void init(){
		Optional<User> maybeUser = userRepository.findByUsername(adminUsername);
		if(!maybeUser.isPresent()) {
			try {
				User user = new User();
				user.setCreatedAt(new Date());

				String sha256EncryptedPass = util.encryptSha256(adminPassword);
				String hashedUserPassword = SpringSecurityConfig.encoder().encode(sha256EncryptedPass);

				user.setUsername(adminUsername);
				user.setPassword(hashedUserPassword);
				user.setSoldTicketsNo(0);
				user.setValidatedTicketsNo(0);
				user.setName("Administrator");
				user.setRole("ROLE_" + SpringSecurityConfig.ADMIN);
				userRepository.save(user);
			} catch(NoSuchAlgorithmException ignored) { }
		}
	}
	
	@GetMapping(path="/")
	public ResponseEntity<String> getConnectionDetails() {
		return new ResponseEntity<String>(appName, HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/login")
	public MappingJacksonValue login(@RequestHeader("Authorization") String authorization) {
		User user = util.getUserFromAuthorization(authorization);
		return setUserFilter(user);
	}
	
	@GetMapping("/users")
	public MappingJacksonValue getUsers(@RequestParam(value="type", required=false) String type, @RequestParam(value="value", required=false) String value, Pageable pageable) {
		List<User> userList = new ArrayList<User>();
		if(type!=null && value != null) {
			switch(type.toUpperCase()) {
				case "ROLE": {
					userList = userRepository.findByRoleOrderByCreatedAtDesc("ROLE_" + value.toUpperCase(), pageable).getContent();
					break;
				}
				case "SEARCH": {
					userList = userRepository.findByNameStartsWithIgnoreCase(value, pageable).getContent();
					break;
				}
			}
		}
		else {
			userList = userRepository.findAllByOrderByCreatedAtDesc(pageable).getContent();
		}
		return setUserFilter(userList);
	}
	
	@GetMapping("/users/{id}")
	public MappingJacksonValue getUserById(@PathVariable long id) {
		Optional<User> optional = userRepository.findById(id);
		if(!optional.isPresent()) {
			throw new ResourceNotFoundException("userId-"+id);
		}
		
		MappingJacksonValue user = setUserFilter(optional.get());
		return user;
	}
	
	@GetMapping("/users/{id}/validated")
	public MappingJacksonValue getValidatedTicketsByUserId(@PathVariable long id) {
		Optional<User> optional = userRepository.findById(id);
		if(!optional.isPresent()) {
			throw new ResourceNotFoundException("userId-"+id);
		}
		
		User user = optional.get();
		List<Ticket> validatedTickets = user.getValidatedTickets();
		MappingJacksonValue map = TicketController.setTicketFilters(validatedTickets, true);
		return map;
	}
	
	@GetMapping("/users/{id}/sold")
	public MappingJacksonValue getSoldTicketsByUserId(@PathVariable long id) {
		Optional<User> optional = userRepository.findById(id);
		if(!optional.isPresent()) {
			throw new ResourceNotFoundException("userId-"+id);
		}
		
		User user = optional.get();
		List<Ticket> soldTickets = user.getSoldTickets();
		MappingJacksonValue map = TicketController.setTicketFilters(soldTickets, true);
		return map;
	}
	
	@PostMapping("/users")
	public ResponseEntity<MappingJacksonValue> createUser(@Valid @RequestBody User user) {
		String username = user.getUsername();
		String password = user.getPassword();
		Optional<User> usersWithSameUsername = userRepository.findByUsername(username);
		if(usersWithSameUsername.isPresent()) {
			throw new UsernameExistsException("username-"+username);
		}
		
		user.setCreatedAt(new Date());
		
		String hashedUserPassword = SpringSecurityConfig.encoder().encode(password);
		user.setPassword(hashedUserPassword);
		user.setSoldTicketsNo(0);
		user.setValidatedTicketsNo(0);
		userRepository.save(user);
		
		return new ResponseEntity<MappingJacksonValue>(setUserFilter(user), HttpStatus.CREATED);
	}
	
	@PostMapping("/users/{userId}")
	public MappingJacksonValue editUser(@PathVariable long userId, @Valid @RequestBody User user) {
		Optional<User> optionalUser = userRepository.findById(userId);
		if(!optionalUser.isPresent()) {
			throw new ResourceNotFoundException("User by usedId: " + userId);
		}
		
		User existingUser = optionalUser.get();
		if(user.getRole().equals("ROLE_" + SpringSecurityConfig.ADMIN)) {
			throw new UnauthorizedRequestException("You can not edit an "+SpringSecurityConfig.ADMIN+" account!");
		}
		
		if(user.getName() != null) {
			existingUser.setName(user.getName());
		}
		if(user.getRole() != null) {
			existingUser.setRole(user.getRole());
		}
		userRepository.save(existingUser);
		return setUserFilter(existingUser);
	}
	
	@DeleteMapping("/users/{userId}")
	public void deleteUser(@PathVariable long userId) {
		Optional<User> optionalUser = userRepository.findById(userId);
		if(!optionalUser.isPresent()) {
			throw new ResourceNotFoundException("User by usedId: " + userId);
		}
		
		User user = optionalUser.get();
		if(user.getRole().equals("ROLE_" + SpringSecurityConfig.ADMIN)) {
			throw new UnauthorizedRequestException("You can not delete an "+SpringSecurityConfig.ADMIN+" account!");
		}
		for(Ticket ticket : user.getSoldTickets()) {
			ticket.setSoldBy(null);
		}
		for(Ticket ticket : user.getValidatedTickets()) {
			ticket.setValidatedBy(null);
		}
		userRepository.delete(user);
	}
	
	public static MappingJacksonValue setUserFilter(Object userObject) {
		MappingJacksonValue mapping = new MappingJacksonValue(userObject);
		SimpleFilterProvider filter = new SimpleFilterProvider().addFilter("UserFilter", getUserFilterProperty());
		mapping.setFilters(filter);
		return mapping;
	}
	
	public static SimpleBeanPropertyFilter getUserFilterProperty() {
		SimpleBeanPropertyFilter filterProperty = SimpleBeanPropertyFilter.filterOutAllExcept("userId","name","role","createdAt","soldTicketsNo","validatedTicketsNo");
		return filterProperty;
	}
	
}
