package com.artarkatesoft.artsfgspring5mvcrest.bootstrap;

import com.artarkatesoft.artsfgspring5mvcrest.api.v1.mapper.CustomerMapper;
import com.artarkatesoft.artsfgspring5mvcrest.repositories.CustomerRepository;
import com.artarkatesoft.model.CustomerListDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class BootstrapCustomers implements CommandLineRunner {

    private final ObjectMapper objectMapper;
    private final CustomerMapper customerMapper;
    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
        bootstrapCustomers();
    }

    private void bootstrapCustomers() throws IOException {
        ClassPathResource resource = new ClassPathResource("/examples/customers.json");
        CustomerListDTO customerListDTO = objectMapper.readValue(resource.getFile(), CustomerListDTO.class);
        customerListDTO
                .getCustomers()
                .stream()
                .map(customerMapper::customerDTOToCustomer)
                .forEach(customerRepository::save);
    }
}
