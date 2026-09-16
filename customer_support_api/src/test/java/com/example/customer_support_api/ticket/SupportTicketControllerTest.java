package com.example.customer_support_api.ticket;

import com.example.customer_support_api.customer.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.example.customer_support_api.customer.CustomerNotFoundException;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
@WebMvcTest(SupportTicketController.class)
class SupportTicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SupportTicketService supportTicketService;

    @Test
    void shouldReturnTicketWhenIdExists() throws Exception {

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

        when(supportTicketService.getTicketById(1L))
                .thenReturn(ticket);

        mockMvc.perform(get("/tickets/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.subject").value("Login issue"))
                .andExpect(jsonPath("$.description").value("The user cannot log in."))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    void shouldReturn404WhenTicketDoesNotExist() throws Exception {

        when(supportTicketService.getTicketById(999L))
                .thenThrow(new TicketNotFoundException("Ticket not found"));

        mockMvc.perform(get("/tickets/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Ticket not found"));
    }

    @Test
    void shouldReturnAllTickets() throws Exception {

        Customer customer =
                new Customer("Fernand", "Battisti", "fernand@example.com");

        SupportTicket ticket1 =
                new SupportTicket(
                        "Login issue",
                        "The user cannot log in.",
                        TicketStatus.OPEN,
                        TicketPriority.HIGH,
                        customer
                );

        SupportTicket ticket2 =
                new SupportTicket(
                        "Payment issue",
                        "The payment failed.",
                        TicketStatus.IN_PROGRESS,
                        TicketPriority.URGENT,
                        customer
                );

        when(supportTicketService.getAllTickets())
                .thenReturn(java.util.List.of(ticket1, ticket2));

        mockMvc.perform(get("/tickets"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].subject").value("Login issue"))
                .andExpect(jsonPath("$[1].subject").value("Payment issue"))
                .andExpect(jsonPath("$[0].priority").value("HIGH"))
                .andExpect(jsonPath("$[1].priority").value("URGENT"));
    }

    @Test
    void shouldCreateTicket() throws Exception {

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

        when(supportTicketService.createTicket(any(CreateTicketRequest.class)))
                .thenReturn(ticket);

        String json = """
                {
                    "subject": "Login issue",
                    "description": "The user cannot log in.",
                    "status": "OPEN",
                    "priority": "HIGH",
                    "customerId": 1
                }
                """;

        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .post("/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.subject").value("Login issue"))
                .andExpect(jsonPath("$.description").value("The user cannot log in."))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    void shouldReturn400WhenCreatingInvalidTicket() throws Exception {

        String json = """
                {
                    "subject": "",
                    "description": "",
                    "status": "OPEN"
                }
                """;

        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .post("/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.subject").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.priority").exists())
                .andExpect(jsonPath("$.customerId").exists());
    }

    @Test
    void shouldUpdateTicket() throws Exception {

        Customer customer =
                new Customer("Fernand", "Battisti", "fernand@example.com");

        SupportTicket ticket =
                new SupportTicket(
                        "New subject",
                        "New description",
                        TicketStatus.IN_PROGRESS,
                        TicketPriority.HIGH,
                        customer
                );

        when(supportTicketService.updateTicket(
                eq(1L),
                any(UpdateTicketRequest.class)
        )).thenReturn(ticket);

        String json = """
                {
                    "subject": "New subject",
                    "description": "New description",
                    "status": "IN_PROGRESS",
                    "priority": "HIGH"
                }
                """;

        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .put("/tickets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subject").value("New subject"))
                .andExpect(jsonPath("$.description").value("New description"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    void shouldReturn404WhenUpdatingTicketDoesNotExist() throws Exception {

        when(supportTicketService.updateTicket(
                eq(999L),
                any(UpdateTicketRequest.class)
        )).thenThrow(new TicketNotFoundException("Ticket not found"));

        String json = """
                {
                    "subject": "Updated subject",
                    "description": "Updated description",
                    "status": "IN_PROGRESS",
                    "priority": "HIGH"
                }
                """;

        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .put("/tickets/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Ticket not found"));
    }

   @Test
    void shouldDeleteTicket() throws Exception {

        mockMvc.perform(delete("/tickets/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(supportTicketService).deleteTicket(1L);
    }

    @Test
    void shouldReturn404WhenDeletingTicketDoesNotExist() throws Exception {

        doThrow(
                new TicketNotFoundException("Ticket not found")
        )
                .when(supportTicketService)
                .deleteTicket(999L);

        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .delete("/tickets/999")
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Ticket not found"));
    }

    @Test
    void shouldReturn404WhenCreatingTicketWithUnknownCustomer() throws Exception {

        when(supportTicketService.createTicket(any(CreateTicketRequest.class)))
                .thenThrow(new CustomerNotFoundException("Customer not found"));

        String json = """
                {
                    "subject": "Login issue",
                    "description": "The user cannot log in.",
                    "status": "OPEN",
                    "priority": "HIGH",
                    "customerId": 999
                }
                """;

        mockMvc.perform(
                post("/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Customer not found"));
    }

    @Test
    void shouldReturn400WhenUpdatingInvalidTicket() throws Exception {

        String json = """
                {
                    "subject": "",
                    "description": "",
                    "status": "OPEN"
                }
                """;

        mockMvc.perform(
                put("/tickets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.subject").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.priority").exists());
    }
}