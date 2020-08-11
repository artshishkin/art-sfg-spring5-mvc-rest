package com.artarkatesoft.artsfgspring5mvcrest.repositories;

import com.artarkatesoft.artsfgspring5mvcrest.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
