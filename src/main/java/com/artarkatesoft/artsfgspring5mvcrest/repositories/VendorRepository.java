package com.artarkatesoft.artsfgspring5mvcrest.repositories;

import com.artarkatesoft.artsfgspring5mvcrest.domain.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
}
