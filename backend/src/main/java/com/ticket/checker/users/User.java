package com.ticket.checker.ticketchecker.users;

import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ticket.checker.ticketchecker.tickets.Ticket;

@Entity
@JsonFilter("UserFilter")
@Table(name = "users")
public class User {
	
	@Id
	@GeneratedValue
    @Column(name = "user_id")
	private Long userId;

	@Column(name = "username")
	@Size(min=3,max=255, message="Username must have at least 3 chars and as much as 255 chars")
	private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "name")
	@Size(max=255, message="Name must have as much as 255 chars")
	private String name;

    @Column(name = "role")
	private String role;

    @Column(name = "created_at")
	private Date createdAt;
	
	@OneToMany(mappedBy = "soldBy", fetch=FetchType.LAZY)
	@OrderBy("sold_at desc")
	@JsonIgnore
	private List<Ticket> soldTickets;

	@Column(name = "sold_tickets_no")
	private int soldTicketsNo;
	
	@OneToMany(mappedBy = "validatedBy", fetch=FetchType.LAZY)
	@OrderBy("validated_at desc")
	@JsonIgnore
	private List<Ticket> validatedTickets;

	@Column(name = "validated_tickets_no")
	private int validatedTicketsNo;
	
	public User() {}

	public User(String username, String password, String name, String role, Date createdAt, List<Ticket> soldTickets, List<Ticket> validatedTickets) {
		this.username = username;
		this.password = password;
		this.name = name;
		this.role = role;
		this.createdAt = createdAt;
		this.soldTickets = soldTickets;
		this.soldTicketsNo = soldTickets.size();
		this.validatedTickets = validatedTickets;
		this.validatedTicketsNo = validatedTickets.size();
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getRole() {
		return role;
	}
	
	public void setRole(String role) {
		this.role = role;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	public void setSoldTickets(List<Ticket> soldTickets) {
		this.soldTickets = soldTickets;
	}
	
	public List<Ticket> getSoldTickets() {
		return soldTickets;
	}
	
	public void setSoldTicketsNo(int soldTicketsNo) {
		this.soldTicketsNo = soldTicketsNo;
	}
	
	public int getSoldTicketsNo() {
		return soldTicketsNo;
	}
	
	public void setValidatedTickets(List<Ticket> validatedTickets) {
		this.validatedTickets = validatedTickets;
	}
	
	public List<Ticket> getValidatedTickets() {
		return validatedTickets;
	}
	
	public void setValidatedTicketsNo(int validatedTicketsNo) {
		this.validatedTicketsNo = validatedTicketsNo;
	}
	
	public int getValidatedTicketsNo() {
		return validatedTicketsNo;
	}
}
