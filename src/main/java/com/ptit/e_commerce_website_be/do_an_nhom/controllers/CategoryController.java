package com.ptit.e_commerce_website_be.do_an_nhom.controllers;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.CategoryDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Category;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.CommonResult;
import com.ptit.e_commerce_website_be.do_an_nhom.services.category.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/{id}")
    public CommonResult<Category> getCategoryById(
            @PathVariable("id") Long categoryId
    ){
        Category existingCategory = categoryService.getCategoryById(categoryId);
        return CommonResult.success(existingCategory, "Get category successfully");
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public CommonResult<Object> createCategory (
            @Valid @RequestBody CategoryDTO categoryDTO
    ){
        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Category category = categoryService.createCategory(categoryDTO, user.getId());
        return CommonResult.success(category,"Create category successfully");
    }

    @GetMapping("")
    public CommonResult<Page<Category>> getAllCatgories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "",required = false) String name
    ){
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("id").ascending());
        Page<Category> categoriesPages = categoryService.getAllCategories(pageRequest, name);
        return CommonResult.success(categoriesPages, "Get all categories");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public  CommonResult deleteCategory(@PathVariable Long id){
        User user  = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        categoryService.deleteCategory(id, user.getId());
        return CommonResult.success("Delete success category");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public CommonResult<Category> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO categoryDTO
    ){
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Category category = categoryService.updateCategory(id, categoryDTO, user.getId());
        return CommonResult.success(category, "Update category successfully");
    }

}
