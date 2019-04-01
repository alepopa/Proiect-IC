package com.ticket.checker.security;

import com.ticket.checker.exceptions.NotFoundException;
import com.ticket.checker.users.User;
import com.ticket.checker.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author Daniel Incicau, daniel.incicau@busymachines.com
 * @since 01/04/2019
 */
@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if(!optionalUser.isPresent()) {
            throw new NotFoundException("Username not found: " + username);
        }

        User user = optionalUser.get();
        CustomUserDetails userDetails = new CustomUserDetails(user);
        return userDetails;
    }

}