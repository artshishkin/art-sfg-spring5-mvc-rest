package com.artarkatesoft.artsfgspring5mvcrest.api.v1.mapper;

import com.artarkatesoft.artsfgspring5mvcrest.domain.Customer;
import com.artarkatesoft.model.CustomerDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
                () -> assertThat(customerDTO.getFirstName()).isEqualTo(FIRST_NAME),
                () -> assertThat(customerDTO.getLastName()).isEqualTo(LAST_NAME),
                () -> assertThat(customerDTO.getCustomerUrl()).isNull(),
                () -> assertThat(customerDTO)
                        .isEqualToComparingOnlyGivenFields(customer,
                                "lastName", "firstName")
        );
    }

    @Test
    void customerDTOToCustomer() {
        //given
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setFirstName(FIRST_NAME);
        customerDTO.setLastName(LAST_NAME);
        //when
        Customer customer = customerMapper.customerDTOToCustomer(customerDTO);
        //then
        assertAll(
                () -> assertThat(customer.getId()).isNull(),
                () -> assertThat(customer.getFirstName()).isEqualTo(FIRST_NAME),
                () -> assertThat(customer.getLastName()).isEqualTo(LAST_NAME),
                () -> assertThat(customer)
                        .isEqualToComparingOnlyGivenFields(customerDTO,
                                "lastName", "firstName")
        );
    }
}
