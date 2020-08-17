package com.artarkatesoft.artsfgspring5mvcrest.api.v1.mapper;

import com.artarkatesoft.artsfgspring5mvcrest.api.v1.model.VendorDTO;
import com.artarkatesoft.artsfgspring5mvcrest.domain.Vendor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VendorMapperTest {

    private VendorMapper vendorMapper;

    @BeforeEach
    void setUp() {
        vendorMapper = VendorMapper.INSTANCE;
    }

    @Test
    void vendorToVendorDTO() {
        //given
        final long id = 123L;
        final String name = "Name";
        Vendor vendor = Vendor.builder().id(id).name(name).build();
        //when
        VendorDTO vendorDTO = vendorMapper.vendorToVendorDTO(vendor);
        //then
        assertThat(vendorDTO.getName()).isEqualTo(name);
    }

    @Test
    void vendorDTOToVendor() {
        //given
        final String name = "Name";
        final String vendorUrl = "some://url";
        VendorDTO vendorDTO = new VendorDTO(name, vendorUrl);
        //when
        Vendor vendor = vendorMapper.vendorDTOToVendor(vendorDTO);
        //then
        assertThat(vendor.getName()).isEqualTo(name);
    }
}