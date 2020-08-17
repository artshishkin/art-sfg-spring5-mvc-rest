package com.artarkatesoft.artsfgspring5mvcrest.services;

import com.artarkatesoft.artsfgspring5mvcrest.api.v1.mapper.CustomerMapper;
import com.artarkatesoft.artsfgspring5mvcrest.api.v1.model.CustomerDTO;
import com.artarkatesoft.artsfgspring5mvcrest.domain.Customer;
import com.artarkatesoft.artsfgspring5mvcrest.repositories.CustomerRepository;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityNotFoundException;
import java.util.stream.LongStream;

import static com.artarkatesoft.artsfgspring5mvcrest.controllers.v1.CustomerController.BASE_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class CustomerServiceImplIT {

    @Autowired
    CustomerRepository customerRepository;

    private CustomerService customerService;
    private Long id;

    @BeforeEach
    void setUp() {
        customerService = new CustomerServiceImpl(customerRepository, CustomerMapper.INSTANCE);
        customerRepository.deleteAll();
        LongStream.rangeClosed(1, 5)
                .mapToObj(id -> new Customer(null, "First" + id, "Last" + id))
                .map(customerRepository::save)
                .forEach(System.out::println);
        id = customerRepository.findAll().iterator().next().getId();
    }

    @Test
    void patchCustomerFirstName_whenPresent() {
        //given
        String firstName = "First" + id + "Updated";
        String lastName = null;
        CustomerDTO customerDTO = new CustomerDTO(firstName, lastName, null);
        //when
        CustomerDTO customerDTOpatcher = customerService.patchCustomer(id, customerDTO);
        //then
        assertThat(customerDTOpatcher.getFirstName()).isEqualTo(firstName);
        assertThat(customerDTOpatcher.getLastName()).isNotEmpty();
        assertThat(customerDTOpatcher.getCustomerUrl()).isEqualTo(BASE_URL + "/" + id);
    }

    @Test
    void patchCustomerLastName_whenPresent() {
        //given
        String firstName = null;
        String lastName = "Last" + id + "Updated";
        CustomerDTO customerDTO = new CustomerDTO(firstName, lastName, null);
        //when
        CustomerDTO customerDTOpatcher = customerService.patchCustomer(id, customerDTO);
        //then
        assertThat(customerDTOpatcher.getFirstName()).isNotEmpty();
        assertThat(customerDTOpatcher.getLastName()).isEqualTo(lastName);
        assertThat(customerDTOpatcher.getCustomerUrl()).isEqualTo(BASE_URL + "/" + id);
    }

    @Test
    void patchCustomer_whenAbsent() {
        //given
        Long idAbsent = 123L;
        String firstName = "First" + idAbsent + "Absent";
        String lastName = "Last" + idAbsent + "Absent";
        CustomerDTO customerDTO = new CustomerDTO(firstName, lastName, null);
        //when
        ThrowableAssert.ThrowingCallable callPatch = () -> customerService.patchCustomer(idAbsent, customerDTO);
        //then
        assertThatThrownBy(callPatch)
                .isExactlyInstanceOf(EntityNotFoundException.class)
                .hasMessage("Customer with id `" + idAbsent + "` not found");
    }
}