package com.ticket.checker.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Daniel Incicau, daniel.incicau@busymachines.com
 * @since 21/03/2019
 */
@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

}
