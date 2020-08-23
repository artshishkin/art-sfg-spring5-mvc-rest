package com.artarkatesoft.artsfgspring5mvcrest.api.v1.mapper;

import com.artarkatesoft.artsfgspring5mvcrest.domain.Customer;
import com.artarkatesoft.model.CustomerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CustomerMapper {
    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    CustomerDTO customerToCustomerDTO(Customer customer);

    Customer customerDTOToCustomer(CustomerDTO customerDTO);
}
