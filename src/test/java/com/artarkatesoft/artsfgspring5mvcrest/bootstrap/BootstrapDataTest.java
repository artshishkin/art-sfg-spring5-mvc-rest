package com.artarkatesoft.artsfgspring5mvcrest.bootstrap;

import com.artarkatesoft.artsfgspring5mvcrest.domain.Category;
import com.artarkatesoft.artsfgspring5mvcrest.repositories.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.atLeast;

@ExtendWith(MockitoExtension.class)
class BootstrapDataTest {

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    BootstrapData bootstrapData;

    @Test
    void testBootstrapData() throws Exception {
        //when
        bootstrapData.run();
        //then
        then(categoryRepository).should(atLeast(3)).save(any(Category.class));
    }
}
