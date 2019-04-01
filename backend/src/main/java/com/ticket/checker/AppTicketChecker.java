package com.ticket.checker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppTicketChecker {

    public static final String REALM_NAME = "Ticket Checker API v1.0";

    public static void main(String[] args) {
        SpringApplication.run(AppTicketChecker.class, args);
    }

}
