package com.artarkatesoft.artsfgspring5mvcrest.api.v1.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryListDTO {
    private List<CategoryDTO> categories;
}
