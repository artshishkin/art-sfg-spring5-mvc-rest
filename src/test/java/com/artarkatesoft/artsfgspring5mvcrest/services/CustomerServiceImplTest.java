package com.artarkatesoft.artsfgspring5mvcrest.services;

import com.artarkatesoft.artsfgspring5mvcrest.api.v1.mapper.CustomerMapper;
import com.artarkatesoft.artsfgspring5mvcrest.api.v1.model.CustomerDTO;
import com.artarkatesoft.artsfgspring5mvcrest.domain.Customer;
import com.artarkatesoft.artsfgspring5mvcrest.repositories.CustomerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    CustomerRepository customerRepository;
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerServiceImpl(customerRepository, CustomerMapper.INSTANCE);
    }

    @Test
    void getAllCustomers() {
        //given
        List<Customer> allCustomers = Arrays.asList(new Customer(), new Customer(), new Customer());
        given(customerRepository.findAll()).willReturn(allCustomers);
        //when
        List<CustomerDTO> allCustomersDTO = customerService.getAllCustomers();
        //then
        assertThat(allCustomersDTO).hasSize(3);
    }

    @Test
    void getCustomerById_whenFound() {
        //given
        Long id = 123L;
        Customer customer = new Customer(id, "Foo", "Bar");
        given(customerRepository.findById(anyLong())).willReturn(Optional.of(customer));
        //when
        CustomerDTO customerDTO = customerService.getCustomerById(id);
        //then
        then(customerRepository).should().findById(eq(id));
        assertThat(customerDTO).isEqualToComparingFieldByField(customer);
    }

    @Test
    void getCustomerById_whenNotFound() {
        //given
        Long id = 123L;
        given(customerRepository.findById(anyLong())).willReturn(Optional.empty());

        //when
        Executable executable = () -> {
            CustomerDTO customerDTO = customerService.getCustomerById(id);
        };

        //then
        assertThrows(RuntimeException.class, executable);
        then(customerRepository).should().findById(eq(id));
    }
}
