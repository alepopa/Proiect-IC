package com.ticket.checker.utils;

import com.ticket.checker.exceptions.AuthenticationException;
import com.ticket.checker.users.User;
import com.ticket.checker.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Optional;

/**
 * @author Daniel Incicau, daniel.incicau@busymachines.com
 * @since 01/04/2019
 */
@Component
public class AuthUtil {

    @Autowired
    private UserRepository userRepository;

    public User getUserFromAuthorization(String authHeader) {
        String[] auth = authHeader.split(" ");
        try {
            byte[] decoded = Base64.getDecoder().decode(auth[1]);
            String decodedString = new String(decoded);
            String[] user_pass = decodedString.split(":");

            String username = user_pass[0];

            Optional<User> optional = userRepository.findByUsername(username);
            if(!optional.isPresent()) {
                throw new AuthenticationException("Login attempt failed!");
            }

            return optional.get();
        }
        catch(ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public String getUsernameFromAuthorization(String authHeader) {
        try {
            String[] auth = authHeader.split(" ");
            byte[] decoded = Base64.getDecoder().decode(auth[1]);
            String decodedString = new String(decoded);
            String[] user_pass = decodedString.split(":");

            String username = user_pass[0];

            Optional<User> optional = userRepository.findByUsername(username);
            if(!optional.isPresent()) {
                return "-";
            }
            else {
                return optional.get().getUsername();
            }
        }
        catch(Exception e) {
            return "-";
        }
    }
}
