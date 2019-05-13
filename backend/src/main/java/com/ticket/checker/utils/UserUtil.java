package com.ticket.checker.utils;

import com.ticket.checker.users.User;
import com.ticket.checker.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserUtil {

    @Autowired
    private UserRepository userRepository;

    public void incrementUserSoldTickets(User user) {
        if(user != null) {
            int soldTicketsNo = user.getSoldTicketsNo() + 1;
            user.setSoldTicketsNo(soldTicketsNo);
            userRepository.save(user);
        }
    }

    public void incrementUserValidatedTickets(User user) {
        if(user != null) {
            int validatedTicketsNo = user.getValidatedTicketsNo() + 1;
            user.setValidatedTicketsNo(validatedTicketsNo);
            userRepository.save(user);
        }
    }

    public void decrementUserSoldTickets(User user) {
        if(user != null) {
            int soldTicketsNo = user.getSoldTicketsNo() - 1;
            user.setSoldTicketsNo(soldTicketsNo);
            userRepository.save(user);
        }
    }

    public void decrementUserValidatedTickets(User user) {
        if(user != null) {
            int validatedTicketsNo = user.getValidatedTicketsNo() - 1;
            user.setValidatedTicketsNo(validatedTicketsNo);
            userRepository.save(user);
        }
    }
}
