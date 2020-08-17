package com.artarkatesoft.artsfgspring5mvcrest.services;

import com.artarkatesoft.artsfgspring5mvcrest.api.v1.mapper.VendorMapper;
import com.artarkatesoft.artsfgspring5mvcrest.api.v1.model.VendorDTO;
import com.artarkatesoft.artsfgspring5mvcrest.domain.Vendor;
import com.artarkatesoft.artsfgspring5mvcrest.repositories.VendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static com.artarkatesoft.artsfgspring5mvcrest.controllers.v1.VendorController.BASE_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class VendorServiceImplTest {

    @Mock
    VendorRepository vendorRepository;
    private VendorService vendorService;

    @Captor
    ArgumentCaptor<Vendor> vendorCaptor;

    @BeforeEach
    void setUp() {
        vendorService = new VendorServiceImpl(vendorRepository, VendorMapper.INSTANCE);
    }

    @Test
    void getAllVendors() {
        //given
        List<Vendor> allVendors = Arrays.asList(new Vendor(), new Vendor(), new Vendor());
        given(vendorRepository.findAll()).willReturn(allVendors);
        //when
        List<VendorDTO> allVendorsDTO = vendorService.getAllVendors();
        //then
        assertThat(allVendorsDTO).hasSize(3);
        then(vendorRepository).should().findAll();
    }

    @Test
    @DisplayName("When get all vendors, every vendor should have `vendor_url`")
    void getAllVendors_testVendorUrl() {
        //given
        List<Vendor> allVendors = LongStream
                .rangeClosed(1, 3)
                .mapToObj(id -> new Vendor(id, "Name" + id))
                .collect(Collectors.toList());
        given(vendorRepository.findAll()).willReturn(allVendors);
        //when
        List<VendorDTO> allVendorsDTO = vendorService.getAllVendors();
        //then
        assertThat(allVendorsDTO).hasSize(3).allSatisfy(dto -> assertAll(
                () -> assertThat(dto).hasNoNullFieldsOrProperties(),
                () -> assertThat(dto.getVendorUrl()).contains(BASE_URL)
        ));
        then(vendorRepository).should().findAll();
    }


    @Test
    void getVendorById_whenFound() {
        //given
        Long id = 123L;
        Vendor vendor = new Vendor(id, "Foo");
        given(vendorRepository.findById(anyLong())).willReturn(Optional.of(vendor));
        //when
        VendorDTO vendorDTO = vendorService.getVendorById(id);
        //then
        then(vendorRepository).should().findById(eq(id));
        assertThat(vendorDTO).isEqualToIgnoringGivenFields(vendor,
                "vendorUrl");
        assertThat(vendorDTO.getVendorUrl())
                .isNotEmpty()
                .endsWith(String.valueOf(id));
    }

    @Test
    void getVendorById_whenNotFound() {
        //given
        Long id = 123L;
        given(vendorRepository.findById(anyLong())).willReturn(Optional.empty());

        //when
        Executable executable = () -> {
            VendorDTO vendorDTO = vendorService.getVendorById(id);
        };

        //then
        assertThrows(EntityNotFoundException.class, executable);
        then(vendorRepository).should().findById(eq(id));
    }

    @Test
    void testCreateNewVendor() {
        //given
        Long id = 123L;
        String name = "First";
        VendorDTO dtoToSave = new VendorDTO(name,null);
        given(vendorRepository.save(any(Vendor.class)))
                .willAnswer(answer -> {
                    Vendor vendor = answer.getArgument(0, Vendor.class);
                    vendor.setId(id);
                    return vendor;
                });

        //when
        VendorDTO savedDto = vendorService.createNewVendor(dtoToSave);

        //then
        then(vendorRepository).should().save(any(Vendor.class));
        assertAll(
                () -> assertThat(savedDto).isEqualToIgnoringNullFields(dtoToSave),
                () -> assertThat(savedDto.getVendorUrl())
                        .isEqualTo(BASE_URL + "/" + id)
        );
    }

    @Test
    void updateVendor_whenPresent() {
        //given
        Long id = 123L;
        String name = "First";

        String previousName = "NameOld";
        VendorDTO dtoToUpdate = new VendorDTO(name,  null);
        Vendor repoVendor = new Vendor(id, previousName);
        given(vendorRepository.findById(anyLong())).willReturn(Optional.of(repoVendor));
        given(vendorRepository.save(any(Vendor.class)))
                .willAnswer(answer -> {
                    Vendor vendor = answer.getArgument(0, Vendor.class);
                    vendor.setId(id);
                    return vendor;
                });

        //when
        VendorDTO updatedDto = vendorService.updateVendor(id, dtoToUpdate);

        //then
        then(vendorRepository).should().findById(eq(id));
        then(vendorRepository).should().save(vendorCaptor.capture());
        Vendor vendorSave = vendorCaptor.getValue();
        assertAll(
                () -> assertThat(vendorSave.getId()).isEqualTo(id),
                () -> assertThat(vendorSave.getName()).isEqualTo(name),
                () -> assertThat(updatedDto).isEqualToIgnoringNullFields(dtoToUpdate),
                () -> assertThat(updatedDto.getVendorUrl())
                        .isEqualTo(BASE_URL + "/" + id)
        );
    }

    @Test
    void updateVendor_whenAbsent() {
        //given
        Long id = 123L;
        String name = "Name";
        String lastName = "Last";

        VendorDTO dtoToUpdate = new VendorDTO(name,  null);
        given(vendorRepository.findById(anyLong())).willReturn(Optional.empty());

        //when
        Executable whenUpdating = () -> vendorService.updateVendor(id, dtoToUpdate);

        //then
        assertThrows(EntityNotFoundException.class, whenUpdating);
        then(vendorRepository).should().findById(eq(id));
    }

    @Test
    void testDeleteVendor_whenPresent() {
        //given
        Long id = 123L;
        String name = "Name";
        Vendor vendor = new Vendor(id, name);
        given(vendorRepository.findById(anyLong())).willReturn(Optional.of(vendor));
        //when
        vendorService.deleteVendor(id);
        //then
        then(vendorRepository).should().findById(eq(id));
        then(vendorRepository).should().delete(eq(vendor));
    }

    @Test
    void testDeleteVendor_whenAbsent() {
        //given
        Long id = 123L;
        given(vendorRepository.findById(anyLong())).willReturn(Optional.empty());
        //when
        ThrowingCallable deleteOperation = () -> vendorService.deleteVendor(id);
        //then
        assertThatThrownBy(deleteOperation)
                .isExactlyInstanceOf(EntityNotFoundException.class)
                .hasMessage("Vendor with id `" + id + "` not found");
        then(vendorRepository).should().findById(eq(id));
        then(vendorRepository).shouldHaveNoMoreInteractions();
    }
}
