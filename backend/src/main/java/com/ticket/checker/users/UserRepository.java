package com.ticket.checker.ticketchecker.users;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>{
	Optional<User> findByUsername(String username);
	Page<User> findAllByOrderByCreatedAtDesc(Pageable pageable);
	Page<User> findByRoleOrderByCreatedAtDesc(String role, Pageable pageable);
	Page<User> findByNameStartsWithIgnoreCase(String name, Pageable pageable);
	Long countByRole(String role);
	Long countByNameStartsWithIgnoreCase(String name);
}
