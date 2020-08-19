package com.artarkatesoft.artsfgspring5mvcrest.api.v1.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "vendor-list", description = "Wrapper around list of Vendors")
public class VendorListDTO {
    private List<VendorDTO> vendors;
}
