package com.artarkatesoft.artsfgspring5mvcrest.services;

import com.artarkatesoft.artsfgspring5mvcrest.api.v1.mapper.CustomerMapper;
import com.artarkatesoft.artsfgspring5mvcrest.api.v1.model.CustomerDTO;
import com.artarkatesoft.artsfgspring5mvcrest.domain.Customer;
import com.artarkatesoft.artsfgspring5mvcrest.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
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
        then(customerRepository).should().findAll();
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
        assertThat(customerDTO).isEqualToIgnoringGivenFields(customer,
                "customerUrl");
        assertThat(customerDTO.getCustomerUrl())
                .isNotEmpty()
                .endsWith(String.valueOf(id));
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

    @Test
    void testCreateNewCustomer() {
        //given
        Long id = 123L;
        String firstName = "First";
        String lastName = "Last";
        CustomerDTO dtoToSave = new CustomerDTO(firstName, lastName, null);
        given(customerRepository.save(any(Customer.class)))
                .willAnswer(answer -> {
                    Customer customer = answer.getArgument(0, Customer.class);
                    customer.setId(id);
                    return customer;
                });

        //when
        CustomerDTO savedDto = customerService.createNewCustomer(dtoToSave);

        //then
        then(customerRepository).should().save(any(Customer.class));
        assertAll(
                () -> assertThat(savedDto).isEqualToIgnoringNullFields(dtoToSave),
                () -> assertThat(savedDto.getCustomerUrl())
                        .isEqualTo("/api/v1/customers/" + id)
        );
    }
}
