package com.example.customer_support_api.ticket;

import com.example.customer_support_api.customer.Customer;
import com.example.customer_support_api.customer.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.example.customer_support_api.customer.CustomerNotFoundException;

@ExtendWith(MockitoExtension.class)
class SupportTicketServiceTest {

    @Mock
    private SupportTicketRepository supportTicketRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private SupportTicketService supportTicketService;

    @Test
    void shouldReturnTicketWhenIdExists() {

        Customer customer =
                new Customer("Fernand", "Battisti", "fernand@example.com");

        SupportTicket ticket =
                new SupportTicket(
                        "Login issue",
                        "The user cannot log in.",
                        TicketStatus.OPEN,
                        TicketPriority.HIGH,
                        customer
                );

        when(supportTicketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));

        SupportTicket result =
                supportTicketService.getTicketById(1L);

        assertEquals("Login issue", result.getSubject());
        assertEquals("The user cannot log in.", result.getDescription());
        assertEquals(TicketStatus.OPEN, result.getStatus());
        assertEquals(TicketPriority.HIGH, result.getPriority());
    }

    @Test
    void shouldThrowExceptionWhenTicketDoesNotExist() {

        when(supportTicketRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                TicketNotFoundException.class,
                () -> supportTicketService.getTicketById(1L)
        );
    }

    @Test
    void shouldUpdateTicket() {

        Customer customer =
                new Customer("Fernand", "Battisti", "fernand@example.com");

        SupportTicket ticket =
                new SupportTicket(
                        "Old subject",
                        "Old description",
                        TicketStatus.OPEN,
                        TicketPriority.LOW,
                        customer
                );

        UpdateTicketRequest request = new UpdateTicketRequest();
        request.setSubject("New subject");
        request.setDescription("New description");
        request.setStatus(TicketStatus.IN_PROGRESS);
        request.setPriority(TicketPriority.HIGH);

        when(supportTicketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));

        when(supportTicketRepository.save(ticket))
                .thenReturn(ticket);

        SupportTicket result =
                supportTicketService.updateTicket(1L, request);

        assertEquals("New subject", result.getSubject());
        assertEquals("New description", result.getDescription());
        assertEquals(TicketStatus.IN_PROGRESS, result.getStatus());
        assertEquals(TicketPriority.HIGH, result.getPriority());
    }

    @Test
    void shouldCreateTicket() {

        Customer customer =
                new Customer("Fernand", "Battisti", "fernand@example.com");

        CreateTicketRequest request = new CreateTicketRequest();
        request.setSubject("New ticket");
        request.setDescription("This is a new ticket.");
        request.setStatus(TicketStatus.OPEN);
        request.setPriority(TicketPriority.MEDIUM);
        request.setCustomerId(1L);

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(supportTicketRepository.save(org.mockito.ArgumentMatchers.any(SupportTicket.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SupportTicket result =
                supportTicketService.createTicket(request);

        assertEquals("New ticket", result.getSubject());
        assertEquals("This is a new ticket.", result.getDescription());
        assertEquals(TicketStatus.OPEN, result.getStatus());
        assertEquals(TicketPriority.MEDIUM, result.getPriority());
        assertEquals(customer, result.getCustomer());
    }

    @Test
    void shouldThrowExceptionWhenCreatingTicketWithUnknownCustomer() {

        CreateTicketRequest request = new CreateTicketRequest();
        request.setSubject("New ticket");
        request.setDescription("This is a new ticket.");
        request.setStatus(TicketStatus.OPEN);
        request.setPriority(TicketPriority.MEDIUM);
        request.setCustomerId(999L);

        when(customerRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                com.example.customer_support_api.customer.CustomerNotFoundException.class,
                () -> supportTicketService.createTicket(request)
        );
    }

    @Test
    void shouldDeleteTicket() {

        Customer customer =
                new Customer("Fernand", "Battisti", "fernand@example.com");

        SupportTicket ticket =
                new SupportTicket(
                        "Login issue",
                        "The user cannot log in.",
                        TicketStatus.OPEN,
                        TicketPriority.HIGH,
                        customer
                );

        when(supportTicketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));

        supportTicketService.deleteTicket(1L);

        org.mockito.Mockito.verify(supportTicketRepository)
                .delete(ticket);
    }

    @Test
    void shouldThrowExceptionWhenDeletingTicketDoesNotExist() {

        when(supportTicketRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                TicketNotFoundException.class,
                () -> supportTicketService.deleteTicket(999L)
        );
    }

}