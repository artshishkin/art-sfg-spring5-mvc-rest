package com.artarkatesoft.artsfgspring5mvcrest.api.v1.mapper;

import com.artarkatesoft.artsfgspring5mvcrest.api.v1.model.CustomerDTO;
import com.artarkatesoft.artsfgspring5mvcrest.domain.Customer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CustomerMapperTest {

    public static final long ID = 123L;
    public static final String FIRST_NAME = "Art";
    public static final String LAST_NAME = "Shyshkin";

    private static CustomerMapper customerMapper;

    @BeforeAll
    static void beforeAll() {
        customerMapper = Mappers.getMapper(CustomerMapper.class);
    }

    @Test
    void customerToCustomerDTO() {
        //given
        Customer customer = new Customer(ID, FIRST_NAME, LAST_NAME);
        //when
        CustomerDTO customerDTO = customerMapper.customerToCustomerDTO(customer);
        //then
        assertAll(
                () -> assertThat(customerDTO.getId()).isEqualTo(ID),
                () -> assertThat(customerDTO.getFirstName()).isEqualTo(FIRST_NAME),
                () -> assertThat(customerDTO.getLastName()).isEqualTo(LAST_NAME),
                () -> assertThat(customerDTO).isEqualToComparingFieldByField(customer)
        );
    }

    @Test
    void customerDTOToCustomer() {
        //given
        CustomerDTO customerDTO = new CustomerDTO(ID, FIRST_NAME, LAST_NAME);
        //when
        Customer customer = customerMapper.customerDTOToCustomer(customerDTO);
        //then
        assertAll(
                () -> assertThat(customer.getId()).isEqualTo(ID),
                () -> assertThat(customer.getFirstName()).isEqualTo(FIRST_NAME),
                () -> assertThat(customer.getLastName()).isEqualTo(LAST_NAME),
                () -> assertThat(customer).isEqualToComparingFieldByField(customerDTO)
        );
    }
}
