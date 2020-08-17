package com.artarkatesoft.artsfgspring5mvcrest.controllers.v1;

import com.artarkatesoft.artsfgspring5mvcrest.api.v1.model.CategoryDTO;
import com.artarkatesoft.artsfgspring5mvcrest.api.v1.model.CategoryListDTO;
import com.artarkatesoft.artsfgspring5mvcrest.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CategoryController.BASE_URL)
@RequiredArgsConstructor
public class CategoryController {

    final static String BASE_URL = "/api/v1/categories";

    private final CategoryService categoryService;

    @GetMapping
    public CategoryListDTO getAllCategories() {
        return new CategoryListDTO(categoryService.getAllCategories());
    }

    @GetMapping("{name}")
    public CategoryDTO getByName(@PathVariable String name) {
        return categoryService.getCategoryByName(name);
    }


}
