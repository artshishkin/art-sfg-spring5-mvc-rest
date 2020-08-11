package com.artarkatesoft.artsfgspring5mvcrest.repositories;

import com.artarkatesoft.artsfgspring5mvcrest.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
