package com.example.customer_support_api.customer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void shouldReturnCustomerWhenIdExists() {
        Customer customer =
                new Customer("Fernand", "Battisti", "fernand@example.com");

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        Customer result = customerService.getCustomerById(1L);

        assertEquals("Fernand", result.getFirstName());
        assertEquals("Battisti", result.getLastName());
        assertEquals("fernand@example.com", result.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenCustomerDoesNotExist() {
        when(customerRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.getCustomerById(1L)
        );
    }
}