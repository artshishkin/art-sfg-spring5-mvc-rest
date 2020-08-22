package com.artarkatesoft.artsfgspring5mvcrest.controllers.v1;

import com.artarkatesoft.artsfgspring5mvcrest.api.v1.model.CategoryDTO;
import com.artarkatesoft.artsfgspring5mvcrest.exceptions.RestResponseEntityExceptionHandler;
import com.artarkatesoft.artsfgspring5mvcrest.services.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;

import static com.artarkatesoft.artsfgspring5mvcrest.controllers.v1.CategoryController.BASE_URL;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        mockMvc = MockMvcBuilders
                .standaloneSetup(categoryController)
                .setControllerAdvice(new RestResponseEntityExceptionHandler())
                .build();

        defaultCategoryDTOList = Arrays.asList(
                new CategoryDTO(1L, "First"),
                new CategoryDTO(2L, "Second"),
                new CategoryDTO(3L, "Third"));
    }

    @Test
    void getAllCategories_json() throws Exception {
        //given
        given(categoryService.getAllCategories()).willReturn(defaultCategoryDTOList);
        int defaultSize = defaultCategoryDTOList.size();

        //when
        mockMvc.perform(get(BASE_URL).accept(APPLICATION_JSON))
                .andExpect(matchAll(
                        status().isOk(),
                        content().contentType(APPLICATION_JSON))
                )
                .andExpect(jsonPath("$.categories", hasSize(defaultSize)));

        //then
        then(categoryService).should().getAllCategories();
    }

    @Test
    void getAllCategories_xml() throws Exception {
        //given
        given(categoryService.getAllCategories()).willReturn(defaultCategoryDTOList);
        int defaultSize = defaultCategoryDTOList.size();

        //when
        mockMvc.perform(get(BASE_URL).accept(APPLICATION_XML))
                .andExpect(matchAll(
                        status().isOk(),
                        content().contentType(APPLICATION_XML_VALUE + ";charset=UTF-8"))
                )
                .andExpect(xpath("//categories//categories").nodeCount(defaultSize));

        //then
        then(categoryService).should().getAllCategories();
    }

    @Test
    void getCategoryByName_whenPresent_json() throws Exception {
        //given
        CategoryDTO defaultCategoryDTO = defaultCategoryDTOList.get(0);
        final Long ID = defaultCategoryDTO.getId();
        final String NAME = defaultCategoryDTO.getName();

        given(categoryService.getCategoryByName(anyString())).willReturn(defaultCategoryDTO);

        //when
        mockMvc.perform(get(BASE_URL + "/{name}", NAME).accept(APPLICATION_JSON))
                .andExpect(matchAll(
                        status().isOk(),
                        content().contentType(APPLICATION_JSON))
                )
                .andExpect(jsonPath("$.name", equalTo(NAME)));

        //then
        then(categoryService).should().getCategoryByName(eq(NAME));
    }

    @Test
    void getCategoryByName_whenPresent_xml() throws Exception {
        //given
        CategoryDTO defaultCategoryDTO = defaultCategoryDTOList.get(0);
        final Long ID = defaultCategoryDTO.getId();
        final String NAME = defaultCategoryDTO.getName();

        given(categoryService.getCategoryByName(anyString())).willReturn(defaultCategoryDTO);

        //when
        mockMvc.perform(get(BASE_URL + "/{name}", NAME).accept(APPLICATION_XML))
                .andExpect(matchAll(
                        status().isOk(),
                        content().contentType(APPLICATION_XML_VALUE + ";charset=UTF-8"))
                )
                .andExpect(xpath("//name").string(NAME));

        //then
        then(categoryService).should().getCategoryByName(eq(NAME));
    }

    @Test
    void getCategoryByName_whenAbsent() throws Exception {
        //given
        final String CATEGORY_NAME = "CategoryNotPresent";

        given(categoryService.getCategoryByName(anyString()))
                .willThrow(new EntityNotFoundException("Category with name `" + CATEGORY_NAME + "` not found"));

        //when
        mockMvc.perform(get(BASE_URL + "/{name}", CATEGORY_NAME).contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Category with name `" + CATEGORY_NAME + "` not found"));

        //then
        then(categoryService).should().getCategoryByName(eq(CATEGORY_NAME));
    }


}
