package com.artarkatesoft.artsfgspring5mvcrest.bootstrap;

import com.artarkatesoft.artsfgspring5mvcrest.domain.Category;
import com.artarkatesoft.artsfgspring5mvcrest.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class BootstrapCategories implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        bootstrapCategories();
    }

    private void bootstrapCategories() {
        Stream.of("Fruits", "Dried", "Fresh", "Exotic", "Nuts")
                .map(Category::new)
                .forEach(categoryRepository::save);
    }
}
