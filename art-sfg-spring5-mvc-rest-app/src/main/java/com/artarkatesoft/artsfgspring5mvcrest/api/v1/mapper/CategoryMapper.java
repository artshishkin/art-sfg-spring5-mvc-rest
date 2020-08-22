package com.artarkatesoft.artsfgspring5mvcrest.api.v1.mapper;

import com.artarkatesoft.artsfgspring5mvcrest.api.v1.model.CategoryDTO;
import com.artarkatesoft.artsfgspring5mvcrest.domain.Category;
import org.mapstruct.Mapper;

@Mapper
public interface CategoryMapper {
    CategoryDTO categoryToCategoryDTO(Category category);
}
