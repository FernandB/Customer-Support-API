package com.example.customer_support_api.ticket;

import org.springframework.stereotype.Service;

import java.util.List;
import com.example.customer_support_api.customer.Customer;
import com.example.customer_support_api.customer.CustomerRepository;
import com.example.customer_support_api.customer.CustomerNotFoundException;

@Service
public class SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;
    private final CustomerRepository customerRepository;

   public SupportTicketService(
        SupportTicketRepository supportTicketRepository,
        CustomerRepository customerRepository) {

        this.supportTicketRepository = supportTicketRepository;
        this.customerRepository = customerRepository;
    }
    public List<SupportTicket> getAllTickets() {
        return supportTicketRepository.findAll();
    }

    public SupportTicket getTicketById(Long id) {
        return supportTicketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found"));
    }

    public SupportTicket createTicket(CreateTicketRequest request) {

    Customer customer = customerRepository.findById(request.getCustomerId())
            .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

    SupportTicket ticket = new SupportTicket(
            request.getSubject(),
            request.getDescription(),
            request.getStatus(),
            request.getPriority(),
            customer
    );

        return supportTicketRepository.save(ticket);
    }

    public void deleteTicket(Long id) {

    SupportTicket ticket = supportTicketRepository.findById(id)
            .orElseThrow(() -> new TicketNotFoundException("Ticket not found"));

    supportTicketRepository.delete(ticket);
    }

    public SupportTicket updateTicket(Long id, UpdateTicketRequest request) {

    SupportTicket ticket = supportTicketRepository.findById(id)
            .orElseThrow(() -> new TicketNotFoundException("Ticket not found"));

    ticket.setSubject(request.getSubject());
    ticket.setDescription(request.getDescription());
    ticket.setStatus(request.getStatus());
    ticket.setPriority(request.getPriority());

        return supportTicketRepository.save(ticket);
    }
}