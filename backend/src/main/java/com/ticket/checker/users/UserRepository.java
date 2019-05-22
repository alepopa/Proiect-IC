package com.ticket.checker.users;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Component
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);
	Page<User> findAllByOrderByCreatedAtDesc(Pageable pageable);
	Page<User> findByRoleOrderByCreatedAtDesc(String role, Pageable pageable);
	Page<User> findByNameStartsWithIgnoreCase(String name, Pageable pageable);
	Long countByRole(String role);
	Long countByNameStartsWithIgnoreCase(String name);
}