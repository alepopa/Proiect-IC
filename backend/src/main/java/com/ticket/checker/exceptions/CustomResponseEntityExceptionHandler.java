package com.ticket.checker.ticketchecker.exceptions;

import java.util.Date;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@RestController
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(Exception.class)
	public final ResponseEntity<Object> handleAllExceptions(Exception e,WebRequest request) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), e.getMessage(), request.getDescription(false));
		return new ResponseEntity<Object>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), ex.getClass().getSimpleName(), ex.getMessage());
		return new ResponseEntity<Object>(exceptionResponse, HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(AuthenticationException.class)
	public final ResponseEntity<Object> handleLoginException(AuthenticationException e,WebRequest request) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), e.getClass().getSimpleName(), e.getMessage());
		return new ResponseEntity<Object>(exceptionResponse, HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public final ResponseEntity<Object> handleUserNotFoundException(ResourceNotFoundException e,WebRequest request) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), e.getClass().getSimpleName(), e.getMessage());
		return new ResponseEntity<Object>(exceptionResponse, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(UsernameExistsException.class)
	public final ResponseEntity<Object> handleUsernameExistsException(UsernameExistsException e,WebRequest request) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), e.getClass().getSimpleName(), e.getMessage());
		return new ResponseEntity<Object>(exceptionResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(UsernameNotFoundException.class)
	public final ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException e,WebRequest request) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), e.getClass().getSimpleName(), e.getMessage());
		return new ResponseEntity<Object>(exceptionResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(UnauthorizedRequestException.class)
	public final ResponseEntity<Object> handleUnauthorizedRequestException(UnauthorizedRequestException e,WebRequest request) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), e.getClass().getSimpleName(), e.getMessage());
		return new ResponseEntity<Object>(exceptionResponse, HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(TicketExistsException.class)
	public final ResponseEntity<Object> handleTicketExistsException(TicketExistsException e,WebRequest request) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), e.getClass().getSimpleName(), e.getMessage());
		return new ResponseEntity<Object>(exceptionResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(TicketValidationException.class)
	public final ResponseEntity<Object> handleTicketValidatedException(TicketValidationException e,WebRequest request) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), e.getClass().getSimpleName(), e.getMessage());
		return new ResponseEntity<Object>(exceptionResponse, HttpStatus.BAD_REQUEST);
	}
	
}
