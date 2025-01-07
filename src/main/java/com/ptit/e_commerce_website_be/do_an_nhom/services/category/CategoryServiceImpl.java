package com.ptit.e_commerce_website_be.do_an_nhom.services.category;

import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.DataNotFoundException;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.CategoryDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Category;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{
    private final CategoryRepository categoryRepository;

    @Override
    public Category getCategoryById(Long id){
        return categoryRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Category not found"));
    }

    @Override
    @Transactional
    public Category createCategory(CategoryDTO categoryDTO, Long userId){
            Category newCategory = Category
                    .builder()
                    .isDelete(Boolean.FALSE)
                    .name(categoryDTO.getName())
                    .status(categoryDTO.getStatus())
                    .slug(categoryDTO.getSlug())
                    .build();
            return categoryRepository.save(newCategory);
    }

    @Override
    public Page<Category> getAllCategories(PageRequest pageRequest, String name){
        return categoryRepository.findByShopId(name, pageRequest);
    }

    @Override
//    @Transactional
//    public Category updateCategory(Long categoryId, CategoryDTO categoryDTO, Long userId){
//        Category existingCategory = getCategoryById(categoryId);
//        existingCategory.setName(categoryDTO.getName());
//        existingCategory.setStatus(categoryDTO.getStatus());
//        categoryRepository.save(existingCategory);
//        return existingCategory;
//    }

    @Transactional
    public Category updateCategory(Long categoryId, CategoryDTO categoryDTO, Long userId) {
        // Lấy category hiện có
        Category existingCategory = getCategoryById(categoryId);

        // Kiểm tra nếu danh mục đã bị xóa (isDelete = true)
        if (Boolean.TRUE.equals(existingCategory.getIsDelete())) {
            throw new IllegalArgumentException("Không thể cập nhật danh mục đã bị xóa.");
        }

        // Kiểm tra xem có category khác với cùng tên tồn tại không
        boolean isDuplicateName = categoryRepository.existsByNameAndIdNotAndIsDelete(
                categoryDTO.getName(),
                categoryId,
                false // chỉ kiểm tra danh mục chưa bị xóa
        );

        if (isDuplicateName) {
            throw new IllegalArgumentException("Tên danh mục đã tồn tại.");
        }

        // Cập nhật thông tin danh mục nếu không có lỗi
        existingCategory.setName(categoryDTO.getName());
        existingCategory.setStatus(categoryDTO.getStatus());
        return categoryRepository.save(existingCategory);
    }


    @Override
    @Transactional
    public Category deleteCategory(Long id, Long userId){
            Category category = getCategoryById(id);
            category.setIsDelete(Boolean.TRUE);
            categoryRepository.save(category);
        return category;
    }


}
