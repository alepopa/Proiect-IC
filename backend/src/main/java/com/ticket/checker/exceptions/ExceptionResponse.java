package com.ticket.checker.ticketchecker.exceptions;

import java.util.Date;

public class ExceptionResponse {
	private Date timestamp;
	private String exceptionName;
	private String exceptionMessage;
	
	public ExceptionResponse(Date timestamp, String exceptionName, String exceptionMessage) {
		super();
		this.timestamp = timestamp;
		this.exceptionName = exceptionName;
		this.exceptionMessage = exceptionMessage;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getMessage() {
		return exceptionName;
	}

	public void setMessage(String message) {
		this.exceptionName = message;
	}

	public String getDetails() {
		return exceptionMessage;
	}

	public void setDetails(String details) {
		this.exceptionMessage = details;
	}
	
}
