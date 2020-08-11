package com.artarkatesoft.artsfgspring5mvcrest.api.v1.mapper;

import com.artarkatesoft.artsfgspring5mvcrest.api.v1.model.CategoryDTO;
import com.artarkatesoft.artsfgspring5mvcrest.domain.Category;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CategoryMapperTest {

    @Test
    void categoryToCategoryDTO() {
        //given
        String NAME = "Foo";
        long ID = 123L;
        Category category = new Category(ID, NAME);
        CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);

        //when
        CategoryDTO categoryDTO = categoryMapper.categoryToCategoryDTO(category);

        //then
        assertAll(
                () -> assertThat(categoryDTO.getId()).isEqualTo(ID),
                () -> assertThat(categoryDTO.getName()).isEqualTo(NAME),
                () -> assertThat(categoryDTO).isEqualToComparingFieldByField(category)
        );
    }
}
