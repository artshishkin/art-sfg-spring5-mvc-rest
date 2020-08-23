package com.artarkatesoft.artsfgspring5mvcrest.controllers.v1;

import com.artarkatesoft.artsfgspring5mvcrest.services.CustomerService;
import com.artarkatesoft.model.CustomerDTO;
import com.artarkatesoft.model.CustomerListDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Api(tags = {"API Customer Controller"})
@SwaggerDefinition(tags = {
        @Tag(name = "API Customer Controller", description = "This Controller is used to operate Customers")
})
@RestController
@RequiredArgsConstructor
@RequestMapping(CustomerController.BASE_URL)
public class CustomerController {

    public final static String BASE_URL = "/api/v1/customers";

    private final CustomerService customerService;

    @ApiOperation(value = "Retrieve all customers", notes = "This will show all the customers")
    @GetMapping
    public CustomerListDTO getAllCustomers() {
        CustomerListDTO customerListDTO = new CustomerListDTO();
        customerListDTO.getCustomers().addAll(customerService.getAllCustomers());
        return customerListDTO;
    }

    @ApiOperation(value = "Retrieve customer by id", notes = "This will show one customer info")
    @GetMapping("{id}")
    public CustomerDTO getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @ApiOperation(value = "Add new customer", notes = "You may add new customer")
    @PostMapping
    public ResponseEntity<CustomerDTO> createNewCustomer(@RequestBody CustomerDTO customerDTO,
                                                         UriComponentsBuilder uriBuilder) {
        CustomerDTO newCustomer = customerService.createNewCustomer(customerDTO);
        String customerUrl = newCustomer.getCustomerUrl();
        URI locationUri = uriBuilder.path(customerUrl).build().toUri();
//        URI locationUri = uriBuilder.build().toUri();
        return ResponseEntity.created(locationUri).body(newCustomer);
    }

    @ApiOperation("Update customer's information")
    @PutMapping("{id}")
    public CustomerDTO updateCustomer(@PathVariable Long id, @RequestBody CustomerDTO customerDTO) {
        return customerService.updateCustomer(id, customerDTO);
    }

    @ApiOperation("Modify certain properties of customer information")
    @PatchMapping("{id}")
    public ResponseEntity<CustomerDTO> patchCustomer(@PathVariable Long id, @RequestBody CustomerDTO customerDTO) {
        CustomerDTO body = customerService.patchCustomer(id, customerDTO);
        return ResponseEntity.ok(body);
    }

    @ApiOperation("Delete customer")
    @DeleteMapping("{id}")
    public void deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }
}
