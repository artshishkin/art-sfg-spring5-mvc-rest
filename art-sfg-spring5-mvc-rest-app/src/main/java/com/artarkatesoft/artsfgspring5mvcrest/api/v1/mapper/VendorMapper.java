package com.artarkatesoft.artsfgspring5mvcrest.api.v1.mapper;

import com.artarkatesoft.artsfgspring5mvcrest.api.v1.model.VendorDTO;
import com.artarkatesoft.artsfgspring5mvcrest.domain.Vendor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface VendorMapper {
    VendorMapper INSTANCE = Mappers.getMapper(VendorMapper.class);

    VendorDTO vendorToVendorDTO(Vendor vendor);

    Vendor vendorDTOToVendor(VendorDTO vendorDTO);
}
