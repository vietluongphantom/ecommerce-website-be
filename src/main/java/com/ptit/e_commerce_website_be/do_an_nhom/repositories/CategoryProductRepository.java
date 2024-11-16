package com.ptit.e_commerce_website_be.do_an_nhom.repositories;

import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryProductRepository extends JpaRepository<ProductCategory, Long> {
    @Query("SELECT pc FROM ProductCategory pc WHERE pc.productId = ?1 AND pc.categoryId = ?2 ")
    ProductCategory findByProductIdAndCategoryId(Long id, Long categoryId);
}

