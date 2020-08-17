package com.artarkatesoft.artsfgspring5mvcrest.controllers.v1;

import com.artarkatesoft.artsfgspring5mvcrest.api.v1.model.CustomerDTO;
import com.artarkatesoft.artsfgspring5mvcrest.api.v1.model.CustomerListDTO;
import com.artarkatesoft.artsfgspring5mvcrest.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public CustomerListDTO getAllCustomers() {
        return new CustomerListDTO(customerService.getAllCustomers());
    }

    @GetMapping("{id}")
    public CustomerDTO getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @PostMapping
    public ResponseEntity<CustomerDTO> createNewCustomer(@RequestBody CustomerDTO customerDTO,
                                                         UriComponentsBuilder uriBuilder) {
        CustomerDTO newCustomer = customerService.createNewCustomer(customerDTO);
        String customerUrl = newCustomer.getCustomerUrl();
        URI locationUri = uriBuilder.path(customerUrl).build().toUri();
//        URI locationUri = uriBuilder.build().toUri();
        return ResponseEntity.created(locationUri).body(newCustomer);
    }

    @PutMapping("{id}")
    public CustomerDTO updateCustomer(@PathVariable Long id, @RequestBody CustomerDTO customerDTO) {
        return customerService.updateCustomer(id, customerDTO);
    }
}
