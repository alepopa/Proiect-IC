package com.ticket.checker.users;

import com.ticket.checker.exceptions.ForbiddenException;
import com.ticket.checker.exceptions.InvalidInputException;
import com.ticket.checker.exceptions.NotFoundException;
import com.ticket.checker.security.SpringSecurityConfig;
import com.ticket.checker.tickets.Ticket;
import com.ticket.checker.utils.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Daniel Incicau, daniel.incicau@busymachines.com
 * @since 21/03/2019
 */
@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthUtil authUtil;

    @Value("${application.name:Ticket Checker}")
    private String appName;

    @GetMapping(path="/")
    public ResponseEntity<String> getConnectionDetails() {
        return new ResponseEntity<String>(appName, HttpStatus.ACCEPTED);
    }

    @GetMapping("/login")
    public User login(@RequestHeader("Authorization") String authorization) {
        User user = authUtil.getUserFromAuthorization(authorization);
        return user;
    }

    @GetMapping("/users")
    public List<User> getUsers(@RequestParam(value="type", required=false) String type, @RequestParam(value="value", required=false) String value, Pageable pageable) {
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
        return userList;
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable long id) {
        Optional<User> optional = userRepository.findById(id);
        if(!optional.isPresent()) {
            throw new NotFoundException("userId-"+id);
        }
        return optional.get();
    }

    @GetMapping("/users/{id}/validated")
    public List<Ticket> getValidatedTicketsByUserId(@PathVariable long id) {
        Optional<User> optional = userRepository.findById(id);
        if(!optional.isPresent()) {
            throw new NotFoundException("userId-"+id);
        }

        User user = optional.get();
        return user.getValidatedTickets();
    }

    @GetMapping("/users/{id}/sold")
    public List<Ticket> getSoldTicketsByUserId(@PathVariable long id) {
        Optional<User> optional = userRepository.findById(id);
        if(!optional.isPresent()) {
            throw new NotFoundException("userId-"+id);
        }

        User user = optional.get();
        return user.getSoldTickets();
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        Optional<User> usersWithSameUsername = userRepository.findByUsername(username);
        if(usersWithSameUsername.isPresent()) {
            throw new InvalidInputException("username-"+username);
        }

        user.setCreatedAt(new Date());

        String hashedUserPassword = SpringSecurityConfig.encoder().encode(password);
        user.setPassword(hashedUserPassword);
        user.setSoldTicketsNo(0);
        user.setValidatedTicketsNo(0);
        userRepository.save(user);

        return new ResponseEntity<User>(user, HttpStatus.CREATED);
    }

    @PostMapping("/users/{userId}")
    public User editUser(@PathVariable long userId, @Valid @RequestBody User user) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(!optionalUser.isPresent()) {
            throw new NotFoundException("User by usedId: " + userId);
        }

        User existingUser = optionalUser.get();
        if(user.getRole().equals("ROLE_" + SpringSecurityConfig.ADMIN)) {
            throw new ForbiddenException("You can not edit an " + SpringSecurityConfig.ADMIN + " account!");
        }

        if(user.getName() != null) {
            existingUser.setName(user.getName());
        }
        if(user.getRole() != null) {
            existingUser.setRole(user.getRole());
        }
        userRepository.save(existingUser);
        return user;
    }

    @DeleteMapping("/users/{userId}")
    public void deleteUser(@PathVariable long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(!optionalUser.isPresent()) {
            throw new NotFoundException("User by usedId: " + userId);
        }

        User user = optionalUser.get();
        if(user.getRole().equals("ROLE_" + SpringSecurityConfig.ADMIN)) {
            throw new ForbiddenException("You can not delete an " + SpringSecurityConfig.ADMIN + " account!");
        }
        for(Ticket ticket : user.getSoldTickets()) {
            ticket.setSoldBy(null);
        }
        for(Ticket ticket : user.getValidatedTickets()) {
            ticket.setValidatedBy(null);
        }
        userRepository.delete(user);
    }

}
