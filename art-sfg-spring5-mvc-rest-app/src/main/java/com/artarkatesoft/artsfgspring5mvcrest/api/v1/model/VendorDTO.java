package com.artarkatesoft.artsfgspring5mvcrest.api.v1.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "vendor", description = "Vendor information")
public class VendorDTO {

    @ApiModelProperty(value = "Vendor's name",notes = "Full name of vendor, company name", required = true)
    @JsonProperty("name")
    private String name;
    @ApiModelProperty(value = "URL of vendor's info", notes = "It is set by API and return to the API client")
    @JsonProperty("vendor_url")
    private String vendorUrl;

}
