package com.artarkatesoft.artsfgspring5mvcrest.bootstrap;

import com.artarkatesoft.artsfgspring5mvcrest.api.v1.mapper.CustomerMapper;
import com.artarkatesoft.artsfgspring5mvcrest.domain.Customer;
import com.artarkatesoft.artsfgspring5mvcrest.repositories.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class BootstrapCustomersTest {

    @Mock
    CustomerRepository customerRepository;
    private BootstrapCustomers bootstrapCustomers;

    @Captor
    ArgumentCaptor<Customer> captor;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        CustomerMapper customerMapper = Mappers.getMapper(CustomerMapper.class);
        bootstrapCustomers = new BootstrapCustomers(objectMapper, customerMapper, customerRepository);
    }

    @Test
    void testBootstrapCustomers() throws Exception {
        //given
        //when
        bootstrapCustomers.run();
        //then
        then(customerRepository).should(times(10)).save(captor.capture());
        List<Customer> allSavedCustomers = captor.getAllValues();
        assertThat(allSavedCustomers).hasSize(10).allSatisfy(
                customer -> assertAll(
                        () -> assertThat(customer.getId()).isNull(),
                        () -> assertThat(customer.getFirstName()).isNotNull(),
                        () -> assertThat(customer.getLastName()).isNotNull()
                ));
    }
}
