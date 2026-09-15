package com.example.customer_support_api.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    @Test
    void shouldReturnCustomers() throws Exception {
        Customer customer =
                new Customer("Fernand", "Battisti", "fernand@example.com");

        when(customerService.getAllCustomers())
                .thenReturn(List.of(customer));

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName")
                        .value("Fernand"))
                .andExpect(jsonPath("$[0].lastName")
                        .value("Battisti"));
    }

    @Test
    void shouldCreateCustomer() throws Exception {
        Customer customer =
                new Customer("Fernand", "Battisti", "fernand@example.com");

        when(customerService.createCustomer(any(Customer.class)))
                .thenReturn(customer);

        mockMvc.perform(
                        post("/api/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customer))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName")
                        .value("Fernand"))
                .andExpect(jsonPath("$.email")
                        .value("fernand@example.com"));
    }
}