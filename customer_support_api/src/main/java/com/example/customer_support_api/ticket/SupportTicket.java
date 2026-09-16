package com.example.customer_support_api.ticket;

import com.example.customer_support_api.customer.Customer;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class SupportTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;

    private String description;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @Enumerated(EnumType.STRING)
    private TicketPriority priority;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    private LocalDateTime createdAt;

    public SupportTicket() {
    }

    public SupportTicket(
            String subject,
            String description,
            TicketStatus status,
            TicketPriority priority,
            Customer customer) {
        this.subject = subject;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.customer = customer;
        
    }

    public Long getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TicketStatus getStatus() {
    return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public TicketPriority getPriority() {
    return priority;
    }

    public void setPriority(TicketPriority priority) {
        this.priority = priority;
    }
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public LocalDateTime getCreatedAt() {
    return createdAt;
    }
    @PrePersist
    protected void onCreate() {
    createdAt = LocalDateTime.now();
}
}