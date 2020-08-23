package com.artarkatesoft.artsfgspring5mvcrest.repositories;

import com.artarkatesoft.artsfgspring5mvcrest.domain.Customer;
import com.artarkatesoft.artsfgspring5mvcrest.domain.CustomerList;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    CustomerRepository customerRepository;

    private static ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll() {
        objectMapper = new ObjectMapper();
    }

    @BeforeEach
    void setUp() throws IOException {
        customerRepository.deleteAll();

        ClassPathResource resource = new ClassPathResource("/examples/customers.json");
        CustomerList customerList = objectMapper.readValue(resource.getFile(), CustomerList.class);
        customerList.getCustomers()
                .forEach(customerRepository::save);
    }

    @Test
    void testFindAll() {
        //when
        List<Customer> customerList = customerRepository.findAll();
        //then
        assertThat(customerList).hasSize(10);
        assertThat(customerList).allSatisfy(
                customer -> assertAll(
                        () -> assertThat(customer.getId()).isNotNull(),
                        () -> assertThat(customer.getFirstName()).isNotNull(),
                        () -> assertThat(customer.getLastName()).isNotNull()
                ));
    }

    @Test
    void testFindById_whenPresent() {
        //given
        Customer defaultCustomer = customerRepository.findAll().get(0);
        Long id = defaultCustomer.getId();
        //when
        Customer foundCustomer = customerRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        //then
        assertThat(foundCustomer).isNotNull().isEqualTo(defaultCustomer);
    }

    @Test
    void testFindById_whenAbsent() {
        //given
        Long id = -123L;
        //when
        Optional<Customer> customerOptional = customerRepository.findById(id);
        //then
        assertThat(customerOptional).isEmpty();
    }
}
