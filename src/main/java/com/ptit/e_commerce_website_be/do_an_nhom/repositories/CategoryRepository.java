package com.ptit.e_commerce_website_be.do_an_nhom.repositories;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.CategoryProductCountDto;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(value = "SELECT * FROM category c " +
            "WHERE c.name LIKE CONCAT('%',?1,'%') " +
            "AND c.is_delete = 0", nativeQuery = true)
    Page<Category> findByShopId(String name, Pageable pageable);

    @Query("SELECT new com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.CategoryProductCountDto(" +
            "c.name, COUNT(pc.productId)) " +
            "FROM Category c " +
            "LEFT JOIN ProductCategory pc ON c.id = pc.categoryId " +
            "WHERE c.isDelete = false " +
            "GROUP BY c.id, c.name")
    List<CategoryProductCountDto> findCategoryProductCounts();
}
