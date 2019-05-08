package com.ticket.checker.ticketchecker.users;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

import com.ticket.checker.ticketchecker.security.SpringSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ticket.checker.ticketchecker.exceptions.AuthenticationException;

@Component
public class UserUtil {
	
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

	public String encryptSha256(String toEncrypt) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] encodedhash = digest.digest(toEncrypt.getBytes(StandardCharsets.UTF_8));
		StringBuilder hexString = new StringBuilder();
		for (byte b : encodedhash) {
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1) hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString().toUpperCase();
	}

}
