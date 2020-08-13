package com.artarkatesoft.artsfgspring5mvcrest.controllers.v1;

import com.artarkatesoft.artsfgspring5mvcrest.api.v1.model.CustomerDTO;
import com.artarkatesoft.artsfgspring5mvcrest.api.v1.model.CustomerListDTO;
import com.artarkatesoft.artsfgspring5mvcrest.services.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CustomerService customerService;

    @Value("classpath:/examples/customers.json")
    Resource resource;

    @Test
    void getAllCustomers_usingFakeImplementation() throws Exception {
        //given
        int size = 5;
        List<CustomerDTO> defaultCustomers = getFakeCustomers(size);
        given(customerService.getAllCustomers()).willReturn(defaultCustomers);

        //when
        mockMvc.perform(get("/api/v1/customers")
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.customers", hasSize(size)));
        //then
        then(customerService).should().getAllCustomers();
    }

    private CustomerDTO createFakeCustomer(Long id) {
        return new CustomerDTO("First" + id, "Last" + id, "/api/v1/customers/" + id);
    }

    private List<CustomerDTO> getFakeCustomers(int size) {
        return LongStream.rangeClosed(1, size)
                .mapToObj(this::createFakeCustomer)
                .collect(Collectors.toList());
    }


    @Test
    void getAllCustomers_usingExampleJson() throws Exception {
        //given
        CustomerListDTO customerListDTO = getExampleCustomers();
        String jsonContent = objectMapper.writeValueAsString(customerListDTO);

        List<CustomerDTO> defaultCustomers = customerListDTO.getCustomers();
        int size = defaultCustomers.size();
        given(customerService.getAllCustomers()).willReturn(defaultCustomers);

        //when
        mockMvc.perform(get("/api/v1/customers")
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(jsonContent))
                .andExpect(jsonPath("$.customers", hasSize(size)))
                .andExpect(jsonPath("$.customers[1].customer_url", CoreMatchers.notNullValue()))
                .andExpect(jsonPath("$.customers[0].firstname", equalTo(defaultCustomers.get(0).getFirstName())));
        //then
        then(customerService).should().getAllCustomers();
    }

    private CustomerListDTO getExampleCustomers() throws IOException {
//        ClassPathResource resource = new ClassPathResource("/examples/customers.json");
        CustomerListDTO exampleCustomerListDTO = objectMapper.readValue(resource.getFile(), CustomerListDTO.class);
        exampleCustomerListDTO
                .getCustomers()
                .forEach(dto ->
                        dto.setCustomerUrl(dto.getCustomerUrl().replace("shop","api/v1")));
        return exampleCustomerListDTO;
    }


    @Test
    void getCustomerById() throws Exception {
        //given
        Long id = 123L;
        CustomerDTO defaultCustomer = createFakeCustomer(id);
        given(customerService.getCustomerById(anyLong())).willReturn(defaultCustomer);
        String firstName = defaultCustomer.getFirstName();
        String lastName = defaultCustomer.getLastName();

        //when
        mockMvc.perform(get("/api/v1/customers/{id}", id).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.firstname", equalTo(firstName)))
                .andExpect(jsonPath("$.lastname", equalTo(lastName)))
                .andExpect(jsonPath("$.customer_url", equalTo("/api/v1/customers/" + id)));
        //then
        then(customerService).should().getCustomerById(eq(id));
    }
}
