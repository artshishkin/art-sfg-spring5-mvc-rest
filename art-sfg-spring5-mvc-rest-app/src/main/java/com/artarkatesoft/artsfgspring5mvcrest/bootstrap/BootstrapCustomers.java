package com.artarkatesoft.artsfgspring5mvcrest.bootstrap;

import com.artarkatesoft.artsfgspring5mvcrest.domain.CustomerList;
import com.artarkatesoft.artsfgspring5mvcrest.repositories.CustomerRepository;
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
    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
        bootstrapCustomers();
    }

    private void bootstrapCustomers() throws IOException {
        ClassPathResource resource = new ClassPathResource("/examples/customers.json");
        CustomerList customerList = objectMapper.readValue(resource.getFile(), CustomerList.class);
        customerList
                .getCustomers()
                .forEach(customerRepository::save);
    }
}
