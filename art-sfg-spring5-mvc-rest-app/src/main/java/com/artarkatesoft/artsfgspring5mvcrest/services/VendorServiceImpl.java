package com.artarkatesoft.artsfgspring5mvcrest.services;

import com.artarkatesoft.artsfgspring5mvcrest.api.v1.mapper.VendorMapper;
import com.artarkatesoft.artsfgspring5mvcrest.api.v1.model.VendorDTO;
import com.artarkatesoft.artsfgspring5mvcrest.domain.Vendor;
import com.artarkatesoft.artsfgspring5mvcrest.repositories.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

import static com.artarkatesoft.artsfgspring5mvcrest.controllers.v1.VendorController.BASE_URL;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;
    private final VendorMapper vendorMapper;

    @Override
    public List<VendorDTO> getAllVendors() {
        return vendorRepository.findAll().stream()
                .map(vendor -> {
                    VendorDTO dto = vendorMapper.vendorToVendorDTO(vendor);
                    Long id = vendor.getId();
                    dto.setVendorUrl(BASE_URL + "/" + id);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public VendorDTO getVendorById(Long id) {
        Vendor vendor = getVendorFromRepo(id);
        VendorDTO dto = vendorMapper
                .vendorToVendorDTO(vendor);
        dto.setVendorUrl(BASE_URL + "/" + id);
        return dto;
    }

    private Vendor getVendorFromRepo(Long id) {
        return vendorRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vendor with id `" + id + "` not found"));
    }

    @Override
    public VendorDTO createNewVendor(VendorDTO vendorDTO) {
        Vendor vendor = vendorMapper.vendorDTOToVendor(vendorDTO);
        return saveAndReturnDTO(vendor);
    }

    private VendorDTO saveAndReturnDTO(Vendor vendor) {
        Vendor savedVendor = vendorRepository.save(vendor);
        Long savedId = savedVendor.getId();
        VendorDTO savedDto = vendorMapper.vendorToVendorDTO(savedVendor);
        savedDto.setVendorUrl(BASE_URL + "/" + savedId);
        return savedDto;
    }

    @Override
    public VendorDTO updateVendor(Long id, VendorDTO vendorDTO) {
        Vendor vendor = getVendorFromRepo(id);//call to ensure that vendor exists
        vendor = vendorMapper.vendorDTOToVendor(vendorDTO);
        vendor.setId(id);
        return saveAndReturnDTO(vendor);
    }

    @Override
    public VendorDTO patchVendor(Long id, VendorDTO vendorDTO) {
        Vendor vendor;
        Assert.notNull(vendorDTO, "Vendor must not be null");
        vendor = getVendorFromRepo(id);
        String name = vendorDTO.getName();
        if (name != null) vendor.setName(name);
        return saveAndReturnDTO(vendor);
    }

    @Override
    public void deleteVendor(Long id) {
        Vendor vendor = vendorRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vendor with id `" + id + "` not found"));
        vendorRepository.delete(vendor);
    }
}
