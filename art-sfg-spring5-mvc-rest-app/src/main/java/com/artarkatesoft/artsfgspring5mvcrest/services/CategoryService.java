package com.artarkatesoft.artsfgspring5mvcrest.services;

import com.artarkatesoft.artsfgspring5mvcrest.api.v1.model.CategoryDTO;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> getAllCategories();

    CategoryDTO getCategoryByName(String name);
}
