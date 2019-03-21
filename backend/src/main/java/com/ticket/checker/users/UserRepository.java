package com.ticket.checker.users;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Daniel Incicau, daniel.incicau@busymachines.com
 * @since 21/03/2019
 */
public interface UserRepository extends JpaRepository<User, Long> {
}
