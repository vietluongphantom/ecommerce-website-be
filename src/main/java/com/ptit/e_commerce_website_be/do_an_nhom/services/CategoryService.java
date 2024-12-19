package com.ptit.e_commerce_website_be.do_an_nhom.services;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.CategoryProductCountDto;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<CategoryProductCountDto> getCategoryProductCounts() {
        return categoryRepository.findCategoryProductCounts();
    }
}
