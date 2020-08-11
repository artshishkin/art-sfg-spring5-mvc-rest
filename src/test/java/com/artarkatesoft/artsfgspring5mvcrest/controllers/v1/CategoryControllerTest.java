package com.artarkatesoft.artsfgspring5mvcrest.controllers.v1;

import com.artarkatesoft.artsfgspring5mvcrest.api.v1.model.CategoryDTO;
import com.artarkatesoft.artsfgspring5mvcrest.services.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.ResultMatcher.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @InjectMocks
    CategoryController categoryController;
    private MockMvc mockMvc;

    @Mock
    CategoryService categoryService;
    private List<CategoryDTO> defaultCategoryDTOList;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();

        defaultCategoryDTOList = Arrays.asList(
                new CategoryDTO(1L, "First"),
                new CategoryDTO(2L, "Second"),
                new CategoryDTO(3L, "Third"));
    }

    @Test
    void getAllCategories() throws Exception {
        //given
        given(categoryService.getAllCategories()).willReturn(defaultCategoryDTOList);
        int defaultSize = defaultCategoryDTOList.size();

        //when
        mockMvc.perform(get("/api/v1/categories").contentType(APPLICATION_JSON))
                .andExpect(matchAll(
                        status().isOk(),
                        content().contentType(APPLICATION_JSON))
                )
                .andExpect(jsonPath("$.categories", hasSize(defaultSize)));

        //then
        then(categoryService).should().getAllCategories();
    }

    @Test
    void getCategoryByName() throws Exception {
        //given
        CategoryDTO defaultCategoryDTO = defaultCategoryDTOList.get(0);
        final Long ID = defaultCategoryDTO.getId();
        final String NAME = defaultCategoryDTO.getName();

        given(categoryService.getCategoryByName(anyString())).willReturn(defaultCategoryDTO);

        //when
        mockMvc.perform(get("/api/v1/categories/{name}", NAME).contentType(APPLICATION_JSON))
                .andExpect(matchAll(
                        status().isOk(),
                        content().contentType(APPLICATION_JSON))
                )
                .andExpect(jsonPath("$.name", equalTo(NAME)));

        //then
        then(categoryService).should().getCategoryByName(eq(NAME));
    }


}
