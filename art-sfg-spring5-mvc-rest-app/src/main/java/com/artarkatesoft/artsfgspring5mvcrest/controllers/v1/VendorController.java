package com.artarkatesoft.artsfgspring5mvcrest.controllers.v1;

import com.artarkatesoft.artsfgspring5mvcrest.api.v1.model.VendorDTO;
import com.artarkatesoft.artsfgspring5mvcrest.api.v1.model.VendorListDTO;
import com.artarkatesoft.artsfgspring5mvcrest.services.VendorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Api(value = "ART Vendor Controller", tags = "API Vendor Controller")
@SwaggerDefinition(tags = {@Tag(name = " API Vendor Controller", description = "Used to operate vendor information")})

@RestController
@RequiredArgsConstructor
@RequestMapping(VendorController.BASE_URL)
public class VendorController {

    public final static String BASE_URL = "/api/v1/vendors";

    private final VendorService vendorService;

    @GetMapping
    @ApiOperation(value = "Get all Vendors", notes = "Getting all the vendors from database")
    public VendorListDTO getAllVendors() {
        return new VendorListDTO(vendorService.getAllVendors());
    }

    @GetMapping("{id}")
    @ApiOperation(value = "Get Vendor by it's id", notes = "Retrieve vendor by id")
    public VendorDTO getVendorById(@PathVariable Long id) {
        return vendorService.getVendorById(id);
    }

    @PostMapping
    @ApiOperation(
            value = "Create new vendor",
            notes = "When creating new vendor you can retrieve it's location by HttpHeader 'Location' too"
    )
    public ResponseEntity<VendorDTO> createNewVendor(@RequestBody VendorDTO vendorDTO,
                                                     UriComponentsBuilder uriBuilder) {
        VendorDTO newVendor = vendorService.createNewVendor(vendorDTO);
        String vendorUrl = newVendor.getVendorUrl();
        URI locationUri = uriBuilder.path(vendorUrl).build().toUri();
//        URI locationUri = uriBuilder.build().toUri();
        return ResponseEntity.created(locationUri).body(newVendor);
    }

    @PutMapping("{id}")
    @ApiOperation(
            value = "Update Vendor's information",
            notes = "Changes all the fields of a vendor"
    )
    public VendorDTO updateVendor(@PathVariable Long id, @RequestBody VendorDTO vendorDTO) {
        return vendorService.updateVendor(id, vendorDTO);
    }

    @PatchMapping("{id}")
    @ApiOperation(
            value = "Modify certain fields",
            notes = "Modifying non-empty fields in body of http request"
    )
    public ResponseEntity<VendorDTO> patchVendor(@PathVariable Long id, @RequestBody VendorDTO vendorDTO) {
        VendorDTO body = vendorService.patchVendor(id, vendorDTO);
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("{id}")
    @ApiOperation(value = "Delete vendor", notes = "Will delete vendor by id")
    public void deleteVendor(@PathVariable Long id) {
        vendorService.deleteVendor(id);
    }
}
