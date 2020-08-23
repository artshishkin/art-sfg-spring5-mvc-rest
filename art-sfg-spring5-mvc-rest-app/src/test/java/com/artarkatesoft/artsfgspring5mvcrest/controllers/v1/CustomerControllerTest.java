package com.artarkatesoft.artsfgspring5mvcrest.controllers.v1;

import com.artarkatesoft.artsfgspring5mvcrest.services.CustomerService;
import com.artarkatesoft.model.CustomerDTO;
import com.artarkatesoft.model.CustomerListDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static com.artarkatesoft.artsfgspring5mvcrest.controllers.v1.CustomerController.BASE_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Captor
    ArgumentCaptor<CustomerDTO> customerDTOCaptor;

    @Test
    void getAllCustomers_usingFakeImplementation() throws Exception {
        //given
        int size = 5;
        List<CustomerDTO> defaultCustomers = getFakeCustomers(size);
        given(customerService.getAllCustomers()).willReturn(defaultCustomers);

        //when
        mockMvc.perform(get(BASE_URL)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.customers", hasSize(size)));
        //then
        then(customerService).should().getAllCustomers();
    }

    private CustomerDTO createFakeCustomer(Long id) {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setFirstName("First" + id);
        customerDTO.setLastName("Last" + id);
        customerDTO.setCustomerUrl(BASE_URL + "/" + id);
        return customerDTO;
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
        mockMvc.perform(get(BASE_URL)
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
                        dto.setCustomerUrl(dto.getCustomerUrl().replace("shop", "api/v1")));
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
        mockMvc.perform(get(BASE_URL + "/{id}", id).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.firstname", equalTo(firstName)))
                .andExpect(jsonPath("$.lastname", equalTo(lastName)))
                .andExpect(jsonPath("$.customer_url", equalTo(BASE_URL + "/" + id)));
        //then
        then(customerService).should().getCustomerById(eq(id));
    }

    @Test
    void getCustomerById_notFound() throws Exception {
        //given
        Long id = 123L;
        given(customerService.getCustomerById(anyLong())).willThrow(new EntityNotFoundException("Customer with id `" + id + "` not found"));

        //when
        mockMvc.perform(get(BASE_URL + "/{id}", id).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Customer with id `" + id + "` not found"));
        //then
        then(customerService).should().getCustomerById(eq(id));
    }

    @ParameterizedTest
    @ValueSource(strings = {BASE_URL, BASE_URL + "/"})
    void createNewCustomer(String urlToPostTo) throws Exception {
        //given
        CustomerDTO dtoToSave = new CustomerDTO();
        dtoToSave.setFirstName("first");
        dtoToSave.setLastName("last");
        CustomerDTO savedDto = new CustomerDTO();
        savedDto.setFirstName("first");
        savedDto.setLastName("last");
        savedDto.setCustomerUrl(BASE_URL + "/123");
        String dtoString = objectMapper.writeValueAsString(dtoToSave);
        given(customerService.createNewCustomer(any(CustomerDTO.class))).willReturn(savedDto);
        //when
        mockMvc
                .perform(
                        post(urlToPostTo)
                                .accept(APPLICATION_JSON)
                                .contentType(APPLICATION_JSON)
                                .content(dtoString))
                .andExpect(ResultMatcher.matchAll(
                        status().isCreated(),
                        content().contentType(APPLICATION_JSON),
                        header().string(HttpHeaders.LOCATION, Matchers.equalTo("http://localhost" + BASE_URL + "/123"))

                ))
                .andExpect(jsonPath("$.firstname", equalTo("first")))
                .andExpect(jsonPath("$.lastname", equalTo("last")))
                .andExpect(jsonPath("$.customer_url", equalTo(BASE_URL + "/123")));
        //then
        then(customerService).should().createNewCustomer(eq(dtoToSave));
    }

    @Test
    void updateCustomer() throws Exception {
        //given
        Long id = 123L;
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setFirstName("Art");
        customerDTO.setLastName("Shyshkin");
        customerDTO.setCustomerUrl(BASE_URL + "/" + id);
        given(customerService.updateCustomer(anyLong(), any(CustomerDTO.class))).willReturn(customerDTO);

        //when
        mockMvc
                .perform(
                        put(BASE_URL + "/{id}", id)
                                .accept(APPLICATION_JSON)
                                .contentType(APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(customerDTO)))
                                .content("{\"firstname\":\"Art\",\"lastname\":\"Shyshkin\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(customerDTO)));
        //then
        then(customerService).should().updateCustomer(eq(id), customerDTOCaptor.capture());
        CustomerDTO customerDTOToSave = customerDTOCaptor.getValue();
        assertThat(customerDTOToSave).isEqualToIgnoringGivenFields(customerDTO, "customerUrl");
    }

    @Test
    void patchCustomer() throws Exception {
        //given
        Long id = 123L;
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setFirstName("Art");
        customerDTO.setLastName("Shyshkin");
        customerDTO.setCustomerUrl(BASE_URL + "/" + id);
        given(customerService.patchCustomer(anyLong(), any(CustomerDTO.class))).willAnswer((Answer<CustomerDTO>) invocation -> {
            Long idPatch = invocation.getArgument(0, Long.class);
            CustomerDTO patchDto = invocation.getArgument(1, CustomerDTO.class);
            String firstName = patchDto.getFirstName();
            String lastName = patchDto.getLastName();

            CustomerDTO newCustomerDTO = new CustomerDTO();
            newCustomerDTO.setFirstName(firstName != null ? firstName : "Art");
            newCustomerDTO.setLastName(lastName != null ? lastName : "Shyshkin");
            newCustomerDTO.setCustomerUrl(BASE_URL + "/" + idPatch);

            return newCustomerDTO;
        });

        //when
        mockMvc
                .perform(
                        patch(BASE_URL + "/{id}", id)
                                .accept(APPLICATION_JSON)
                                .contentType(APPLICATION_JSON)
                                .content("{\"firstname\":\"ArtNew\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.firstname", equalTo("ArtNew")))
                .andExpect(jsonPath("$.lastname", equalTo("Shyshkin")))
                .andExpect(jsonPath("$.customer_url", equalTo(BASE_URL + "/" + id)));

        //then
        then(customerService).should().patchCustomer(eq(id), customerDTOCaptor.capture());
        CustomerDTO customerDTOToPatch = customerDTOCaptor.getValue();
        assertThat(customerDTOToPatch.getFirstName()).isEqualTo("ArtNew");
    }

    @Test
    void patchCustomer_simple() throws Exception {
        //given
        Long id = 123L;
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setFirstName("Art");
        customerDTO.setLastName("Shyshkin");
        customerDTO.setCustomerUrl(BASE_URL + "/" + id);

        given(customerService.patchCustomer(anyLong(), any(CustomerDTO.class))).willAnswer((Answer<CustomerDTO>) invocation -> {
            String firstName = invocation.getArgument(1, CustomerDTO.class).getFirstName();
            customerDTO.setFirstName(firstName);
            return customerDTO;
        });

        //when
        mockMvc
                .perform(
                        patch(BASE_URL + "/{id}", id)
                                .accept(APPLICATION_JSON)
                                .contentType(APPLICATION_JSON)
                                .content("{\"firstname\":\"ArtNew\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.firstname", equalTo("ArtNew")))
                .andExpect(jsonPath("$.lastname", equalTo("Shyshkin")))
                .andExpect(jsonPath("$.customer_url", equalTo(BASE_URL + "/" + id)));

        //then
        then(customerService).should().patchCustomer(eq(id), customerDTOCaptor.capture());
        CustomerDTO customerDTOToPatch = customerDTOCaptor.getValue();
        assertThat(customerDTOToPatch.getFirstName()).isEqualTo("ArtNew");
    }

    @Test
    void deleteCustomer() throws Exception {
        //given
        Long id = 123L;
        //when
        mockMvc.perform(delete(BASE_URL + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        //then
        then(customerService).should().deleteCustomer(eq(id));
        then(customerService).shouldHaveNoMoreInteractions();
    }
}
