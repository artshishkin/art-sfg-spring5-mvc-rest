package com.artarkatesoft.artsfgspring5mvcrest.controllers.v1;

import com.artarkatesoft.artsfgspring5mvcrest.api.v1.model.VendorDTO;
import com.artarkatesoft.artsfgspring5mvcrest.api.v1.model.VendorListDTO;
import com.artarkatesoft.artsfgspring5mvcrest.services.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping(VendorController.BASE_URL)
public class VendorController {

    public final static String BASE_URL =  "/api/v1/vendors";

    private final VendorService vendorService;

    @GetMapping
    public VendorListDTO getAllVendors() {
        return new VendorListDTO(vendorService.getAllVendors());
    }

    @GetMapping("{id}")
    public VendorDTO getVendorById(@PathVariable Long id) {
        return vendorService.getVendorById(id);
    }

    @PostMapping
    public ResponseEntity<VendorDTO> createNewVendor(@RequestBody VendorDTO vendorDTO,
                                                         UriComponentsBuilder uriBuilder) {
        VendorDTO newVendor = vendorService.createNewVendor(vendorDTO);
        String vendorUrl = newVendor.getVendorUrl();
        URI locationUri = uriBuilder.path(vendorUrl).build().toUri();
//        URI locationUri = uriBuilder.build().toUri();
        return ResponseEntity.created(locationUri).body(newVendor);
    }

    @PutMapping("{id}")
    public VendorDTO updateVendor(@PathVariable Long id, @RequestBody VendorDTO vendorDTO) {
        return vendorService.updateVendor(id, vendorDTO);
    }

    @PatchMapping("{id}")
    public ResponseEntity<VendorDTO> patchVendor(@PathVariable Long id, @RequestBody VendorDTO vendorDTO) {
        VendorDTO body = vendorService.patchVendor(id, vendorDTO);
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("{id}")
    public void deleteVendor(@PathVariable Long id) {
        vendorService.deleteVendor(id);
    }
}
