package com.ticket.checker.tickets;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.ticket.checker.users.User;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @Column(name = "ticket_id")
    @Size(min=1, max=255, message="Ticked id must have at least 1 chars and as much as 255 chars")
    private String ticketId;

    @Column(name = "sold_to")
    @Size(max=255, message="Sold to must have as much as 255 chars")
    private String soldTo;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private User soldBy;

    @Column(name = "sold_at", nullable = false)
    private Date soldAt;

    @ManyToOne(fetch = FetchType.EAGER)
    private User validatedBy;

    @Column(name = "validated_at")
    private Date validatedAt;


    public Ticket() {}

    public Ticket(String ticketId, String soldTo, User soldBy, Date soldAt, User validatedBy, Date validatedAt) {
        super();
        this.ticketId = ticketId;
        this.soldTo = soldTo;
        this.soldBy = soldBy;
        this.soldAt = soldAt;
        this.validatedBy = validatedBy;
        this.validatedAt = validatedAt;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getSoldTo() {
        return soldTo;
    }

    public void setSoldTo(String soldTo) {
        this.soldTo = soldTo;
    }

    public User getSoldBy() {
        return soldBy;
    }

    public void setSoldBy(User soldBy) {
        this.soldBy = soldBy;
    }

    public Date getSoldAt() {
        return soldAt;
    }

    public void setSoldAt(Date soldAt) {
        this.soldAt = soldAt;
    }

    public User getValidatedBy() {
        return validatedBy;
    }

    public void setValidatedBy(User validatedBy) {
        this.validatedBy = validatedBy;
    }

    public Date getValidatedAt() {
        return validatedAt;
    }

    public void setValidatedAt(Date validatedAt) {
        this.validatedAt = validatedAt;
    }

}
