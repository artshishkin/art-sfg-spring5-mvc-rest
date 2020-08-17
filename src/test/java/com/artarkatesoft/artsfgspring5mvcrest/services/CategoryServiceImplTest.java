package com.artarkatesoft.artsfgspring5mvcrest.services;

import com.artarkatesoft.artsfgspring5mvcrest.api.v1.mapper.CategoryMapper;
import com.artarkatesoft.artsfgspring5mvcrest.api.v1.model.CategoryDTO;
import com.artarkatesoft.artsfgspring5mvcrest.domain.Category;
import com.artarkatesoft.artsfgspring5mvcrest.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    CategoryService categoryService;

    @Mock
    CategoryRepository categoryRepository;

    private List<Category> defaultCategoryList;

    @BeforeEach
    void setUp() {
        CategoryMapper mapper = Mappers.getMapper(CategoryMapper.class);
        categoryService = new CategoryServiceImpl(categoryRepository, mapper);
        defaultCategoryList = Stream.of("Fruits", "Dried", "Fresh", "Exotic", "Nuts")
                .map(Category::new)
                .collect(Collectors.toList());
    }

    @Test
    void getAllCategories() {
        //given
        given(categoryRepository.findAll()).willReturn(defaultCategoryList);
        //when
        List<CategoryDTO> allCategories = categoryService.getAllCategories();
        //then
        then(categoryRepository).should().findAll();
        assertThat(allCategories).hasSize(5);
    }

    @Test
    void getCategoryByName_whenPresent() {
        //given
        String CATEGORY_NAME = "Nuts";
        long ID = 123L;
        Category defaultCategory = new Category(ID, CATEGORY_NAME);
        given(categoryRepository.findByName(anyString())).willReturn(Optional.of(defaultCategory));
        //when
        CategoryDTO categoryByName = categoryService.getCategoryByName(CATEGORY_NAME);
        //then
        then(categoryRepository).should().findByName(eq(CATEGORY_NAME));
        assertAll(
                () -> assertThat(categoryByName.getId()).isNotNull().isEqualTo(ID),
                () -> assertThat(categoryByName.getName()).isNotNull().isEqualTo(CATEGORY_NAME)
        );

    }

    @Test
    void getCategoryByName_whenAbsent() {
        //given
        String CATEGORY_NAME = "Nuts";
        long ID = 123L;
        given(categoryRepository.findByName(anyString())).willReturn(Optional.empty());
        //when
        assertThatThrownBy(() -> categoryService.getCategoryByName(CATEGORY_NAME))
                //then
                .isExactlyInstanceOf(EntityNotFoundException.class)
                .hasMessage("Category with name `" + CATEGORY_NAME + "` not found");
        then(categoryRepository).should().findByName(eq(CATEGORY_NAME));


    }
}
