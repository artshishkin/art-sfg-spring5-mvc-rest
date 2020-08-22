package com.artarkatesoft.artsfgspring5mvcrest.services;

import com.artarkatesoft.artsfgspring5mvcrest.api.v1.mapper.VendorMapper;
import com.artarkatesoft.artsfgspring5mvcrest.api.v1.model.VendorDTO;
import com.artarkatesoft.artsfgspring5mvcrest.domain.Vendor;
import com.artarkatesoft.artsfgspring5mvcrest.repositories.VendorRepository;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityNotFoundException;
import java.util.stream.LongStream;

import static com.artarkatesoft.artsfgspring5mvcrest.controllers.v1.VendorController.BASE_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class VendorServiceImplIT {

    @Autowired
    VendorRepository vendorRepository;

    private VendorService vendorService;
    private Long id;

    @BeforeEach
    void setUp() {
        vendorService = new VendorServiceImpl(vendorRepository, VendorMapper.INSTANCE);
        vendorRepository.deleteAll();
        LongStream.rangeClosed(1, 5)
                .mapToObj(id -> new Vendor(null, "First" + id))
                .map(vendorRepository::save)
                .forEach(System.out::println);
        id = vendorRepository.findAll().iterator().next().getId();
    }

    @Test
    void patchVendorName_whenPresent() {
        //given
        String name = "First" + id + "Updated";
        String lastName = null;
        VendorDTO vendorDTO = new VendorDTO(name, null);
        //when
        VendorDTO vendorDTOpatcher = vendorService.patchVendor(id, vendorDTO);
        //then
        assertThat(vendorDTOpatcher.getName()).isEqualTo(name);
        assertThat(vendorDTOpatcher.getVendorUrl()).isEqualTo(BASE_URL + "/" + id);
    }

    @Test
    void patchVendor_whenAbsent() {
        //given
        Long idAbsent = 123L;
        String name = "Name" + idAbsent + "Absent";
        VendorDTO vendorDTO = new VendorDTO(name, null);
        //when
        ThrowableAssert.ThrowingCallable callPatch = () -> vendorService.patchVendor(idAbsent, vendorDTO);
        //then
        assertThatThrownBy(callPatch)
                .isExactlyInstanceOf(EntityNotFoundException.class)
                .hasMessage("Vendor with id `" + idAbsent + "` not found");
    }
}