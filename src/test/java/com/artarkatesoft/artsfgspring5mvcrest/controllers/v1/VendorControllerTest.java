package com.artarkatesoft.artsfgspring5mvcrest.controllers.v1;

import com.artarkatesoft.artsfgspring5mvcrest.api.v1.model.VendorDTO;
import com.artarkatesoft.artsfgspring5mvcrest.api.v1.model.VendorListDTO;
import com.artarkatesoft.artsfgspring5mvcrest.services.VendorService;
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

import static com.artarkatesoft.artsfgspring5mvcrest.controllers.v1.VendorController.BASE_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VendorController.class)
class VendorControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    VendorService vendorService;

    @Value("classpath:/examples/vendors.json")
    Resource resource;

    @Captor
    ArgumentCaptor<VendorDTO> vendorDTOCaptor;

    @Test
    void getAllVendors_usingFakeImplementation() throws Exception {
        //given
        int size = 5;
        List<VendorDTO> defaultVendors = getFakeVendors(size);
        given(vendorService.getAllVendors()).willReturn(defaultVendors);

        //when
        mockMvc.perform(get(BASE_URL)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.vendors", hasSize(size)));
        //then
        then(vendorService).should().getAllVendors();
    }

    private VendorDTO createFakeVendor(Long id) {
        return new VendorDTO("First" + id, BASE_URL + "/" + id);
    }

    private List<VendorDTO> getFakeVendors(int size) {
        return LongStream.rangeClosed(1, size)
                .mapToObj(this::createFakeVendor)
                .collect(Collectors.toList());
    }


    @Test
    void getAllVendors_usingExampleJson() throws Exception {
        //given
        VendorListDTO vendorListDTO = getExampleVendors();
        String jsonContent = objectMapper.writeValueAsString(vendorListDTO);

        List<VendorDTO> defaultVendors = vendorListDTO.getVendors();
        int size = defaultVendors.size();
        given(vendorService.getAllVendors()).willReturn(defaultVendors);

        //when
        mockMvc.perform(get(BASE_URL)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(jsonContent))
                .andExpect(jsonPath("$.vendors", hasSize(size)))
                .andExpect(jsonPath("$.vendors[1].vendor_url", CoreMatchers.notNullValue()))
                .andExpect(jsonPath("$.vendors[0].name", equalTo(defaultVendors.get(0).getName())));
        //then
        then(vendorService).should().getAllVendors();
    }

    private VendorListDTO getExampleVendors() throws IOException {
//        ClassPathResource resource = new ClassPathResource("/examples/vendors.json");
        VendorListDTO exampleVendorListDTO = objectMapper.readValue(resource.getFile(), VendorListDTO.class);
        exampleVendorListDTO
                .getVendors()
                .forEach(dto ->
                        dto.setVendorUrl(dto.getVendorUrl().replace("shop", "api/v1")));
        return exampleVendorListDTO;
    }


    @Test
    void getVendorById() throws Exception {
        //given
        Long id = 123L;
        VendorDTO defaultVendor = createFakeVendor(id);
        given(vendorService.getVendorById(anyLong())).willReturn(defaultVendor);
        String name = defaultVendor.getName();

        //when
        mockMvc.perform(get(BASE_URL + "/{id}", id).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.name", equalTo(name)))
                .andExpect(jsonPath("$.vendor_url", equalTo(BASE_URL + "/" + id)));
        //then
        then(vendorService).should().getVendorById(eq(id));
    }

    @Test
    void getVendorById_notFound() throws Exception {
        //given
        Long id = 123L;
        given(vendorService.getVendorById(anyLong())).willThrow(new EntityNotFoundException("Vendor with id `" + id + "` not found"));

        //when
        mockMvc.perform(get(BASE_URL + "/{id}", id).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Vendor with id `" + id + "` not found"));
        //then
        then(vendorService).should().getVendorById(eq(id));
    }

    @ParameterizedTest
    @ValueSource(strings = {BASE_URL, BASE_URL + "/"})
    void createNewVendor(String urlToPostTo) throws Exception {
        //given
        VendorDTO dtoToSave = new VendorDTO("name", null);
        VendorDTO savedDto = new VendorDTO("name", BASE_URL + "/123");
        String dtoString = objectMapper.writeValueAsString(dtoToSave);
        given(vendorService.createNewVendor(any(VendorDTO.class))).willReturn(savedDto);
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
                .andExpect(jsonPath("$.name", equalTo("name")))
                .andExpect(jsonPath("$.vendor_url", equalTo(BASE_URL + "/123")));
        //then
        then(vendorService).should().createNewVendor(eq(dtoToSave));
    }

    @Test
    void updateVendor() throws Exception {
        //given
        Long id = 123L;
        VendorDTO vendorDTO = new VendorDTO("Art", BASE_URL + "/" + id);
        given(vendorService.updateVendor(anyLong(), any(VendorDTO.class))).willReturn(vendorDTO);

        //when
        mockMvc
                .perform(
                        put(BASE_URL + "/{id}", id)
                                .accept(APPLICATION_JSON)
                                .contentType(APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(vendorDTO)))
                                .content("{\"name\":\"Art\",\"lastname\":\"Shyshkin\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(vendorDTO)));
        //then
        then(vendorService).should().updateVendor(eq(id), vendorDTOCaptor.capture());
        VendorDTO vendorDTOToSave = vendorDTOCaptor.getValue();
        assertThat(vendorDTOToSave).isEqualToIgnoringGivenFields(vendorDTO, "vendorUrl");
    }

    @Test
    void patchVendor() throws Exception {
        //given
        Long id = 123L;
        VendorDTO vendorDTO = new VendorDTO("Art", BASE_URL + "/" + id);

        given(vendorService.patchVendor(anyLong(), any(VendorDTO.class))).willAnswer((Answer<VendorDTO>) invocation -> {
            Long idPatch = invocation.getArgument(0, Long.class);
            VendorDTO patchDto = invocation.getArgument(1, VendorDTO.class);
            String name = patchDto.getName();
            return new VendorDTO(
                    name != null ? name : "Art",
                    BASE_URL + "/" + idPatch
            );
        });

        //when
        mockMvc
                .perform(
                        patch(BASE_URL + "/{id}", id)
                                .accept(APPLICATION_JSON)
                                .contentType(APPLICATION_JSON)
                                .content("{\"name\":\"ArtNew\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.name", equalTo("ArtNew")))
                .andExpect(jsonPath("$.vendor_url", equalTo(BASE_URL + "/" + id)));

        //then
        then(vendorService).should().patchVendor(eq(id), vendorDTOCaptor.capture());
        VendorDTO vendorDTOToPatch = vendorDTOCaptor.getValue();
        assertThat(vendorDTOToPatch.getName()).isEqualTo("ArtNew");
    }

    @Test
    void patchVendor_simple() throws Exception {
        //given
        Long id = 123L;
        VendorDTO vendorDTO = new VendorDTO("Art", BASE_URL + "/" + id);

        given(vendorService.patchVendor(anyLong(), any(VendorDTO.class))).willAnswer((Answer<VendorDTO>) invocation -> {
            String name = invocation.getArgument(1, VendorDTO.class).getName();
            vendorDTO.setName(name);
            return vendorDTO;
        });

        //when
        mockMvc
                .perform(
                        patch(BASE_URL + "/{id}", id)
                                .accept(APPLICATION_JSON)
                                .contentType(APPLICATION_JSON)
                                .content("{\"name\":\"ArtNew\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.name", equalTo("ArtNew")))
                .andExpect(jsonPath("$.vendor_url", equalTo(BASE_URL + "/" + id)));

        //then
        then(vendorService).should().patchVendor(eq(id), vendorDTOCaptor.capture());
        VendorDTO vendorDTOToPatch = vendorDTOCaptor.getValue();
        assertThat(vendorDTOToPatch.getName()).isEqualTo("ArtNew");
    }

    @Test
    void deleteVendor() throws Exception {
        //given
        Long id = 123L;
        //when
        mockMvc.perform(delete(BASE_URL + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        //then
        then(vendorService).should().deleteVendor(eq(id));
        then(vendorService).shouldHaveNoMoreInteractions();
    }
}
