package com.artarkatesoft.artsfgspring5mvcrest.bootstrap;

import com.artarkatesoft.artsfgspring5mvcrest.api.v1.mapper.VendorMapper;
import com.artarkatesoft.artsfgspring5mvcrest.api.v1.model.VendorListDTO;
import com.artarkatesoft.artsfgspring5mvcrest.repositories.VendorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class BootstrapVendors implements CommandLineRunner {

    private final ObjectMapper objectMapper;
    private final VendorMapper vendorMapper;
    private final VendorRepository vendorRepository;

    @Override
    public void run(String... args) throws Exception {
        bootstrapVendors();
    }

    private void bootstrapVendors() throws IOException {
        ClassPathResource resource = new ClassPathResource("/examples/vendors.json");
        VendorListDTO vendorListDTO = objectMapper.readValue(resource.getFile(), VendorListDTO.class);
        vendorListDTO
                .getVendors()
                .stream()
                .map(vendorMapper::vendorDTOToVendor)
                .forEach(vendorRepository::save);
    }
}
