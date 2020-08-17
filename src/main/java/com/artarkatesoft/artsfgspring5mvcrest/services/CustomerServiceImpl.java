package com.artarkatesoft.artsfgspring5mvcrest.services;

import com.artarkatesoft.artsfgspring5mvcrest.api.v1.mapper.CustomerMapper;
import com.artarkatesoft.artsfgspring5mvcrest.api.v1.model.CustomerDTO;
import com.artarkatesoft.artsfgspring5mvcrest.domain.Customer;
import com.artarkatesoft.artsfgspring5mvcrest.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customer -> {
                    CustomerDTO dto = customerMapper.customerToCustomerDTO(customer);
                    Long id = customer.getId();
                    dto.setCustomerUrl("/api/v1/customers/" + id);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public CustomerDTO getCustomerById(Long id) {
        Customer customer = getCustomerFromRepo(id);
        CustomerDTO dto = customerMapper
                .customerToCustomerDTO(customer);
        dto.setCustomerUrl("/api/v1/customers/" + id);
        return dto;
    }

    private Customer getCustomerFromRepo(Long id) {
        return customerRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Customer with id " + id + " not found"));
    }

    @Override
    public CustomerDTO createNewCustomer(CustomerDTO customerDTO) {
        Customer customer = customerMapper.customerDTOToCustomer(customerDTO);
        return saveAndReturnDTO(customer);
    }

    private CustomerDTO saveAndReturnDTO(Customer customer) {
        Customer savedCustomer = customerRepository.save(customer);
        Long savedId = savedCustomer.getId();
        CustomerDTO savedDto = customerMapper.customerToCustomerDTO(savedCustomer);
        savedDto.setCustomerUrl("/api/v1/customers/" + savedId);
        return savedDto;
    }

    @Override
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer customer = getCustomerFromRepo(id);//call to ensure that customer exists
        customer = customerMapper.customerDTOToCustomer(customerDTO);
        customer.setId(id);
        return saveAndReturnDTO(customer);
    }
}
