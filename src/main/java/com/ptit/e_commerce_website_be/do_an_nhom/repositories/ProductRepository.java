package com.ptit.e_commerce_website_be.do_an_nhom.repositories;


import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p JOIN ProductCategory pc ON p.id = pc.productId WHERE pc.categoryId = :categoryId")
    List<Product> findByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT p FROM Product p WHERE p.brandId = ?1")
    List<Product> findByBrandId(Long brandId);

    List<Product> findByNameContaining(String name);

    List<Product> findByDescriptionContaining(String description);


    @Query("SELECT p FROM Product p " +
            "LEFT JOIN p.categoryList c " +
            "LEFT JOIN Rate r ON p.id = r.productId " +
            "WHERE (:categoryIds IS NULL OR c.id IN :categoryIds) " +
            "AND (:rate IS NULL OR r.averageStars >= :rate) " +
            "AND (:brandIds IS NULL OR p.brandId IN :brandIds) " +
            "AND (:keyword IS NULL OR :keyword = '' OR (p.name LIKE %:keyword%)) " +
            "AND p.status = 1 AND p.isDelete = False " +
            "AND ((:fromPrice IS NULL OR :toPrice IS NULL) OR (p.minPrice BETWEEN :fromPrice AND :toPrice)) " +
            "AND p.minPrice > 0 " +
            "GROUP BY p.id ")
//            "HAVING (:categoryIds IS NULL OR COUNT(DISTINCT c.id) = :categoryCount)")
    Page<Product> searchProducts(
            @Param("categoryIds") List<Long> categoryIds,
//            @Param("categoryCount") long categoryCount,
            @Param("brandIds") List<Long> brandIds,
            @Param("keyword") String keyword,
            @Param("fromPrice") Long fromPrice,
            @Param("toPrice") Long toPrice,
            @Param("rate") Float rateStar,
            Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "LEFT JOIN p.categoryList c " +
            "WHERE (:categoryIds IS NULL OR c.id IN :categoryIds) " +
            "AND (:brandIds IS NULL OR p.brandId IN :brandIds) " +
            "AND (:keyword IS NULL OR :keyword = '' OR p.name LIKE %:keyword%) " +
            "AND p.isDelete = False " +
            "AND p.shopId = :shopId " +
            "GROUP BY p.id " +
            "HAVING (:categoryIds IS NULL OR COUNT(DISTINCT c.id) = :categoryCount)")
    Page<Product> searchProductsSeller(
            @Param("categoryIds") List<Long> categoryIds,
            @Param("categoryCount") long categoryCount,
            @Param("brandIds") List<Long> brandIds,
            @Param("keyword") String keyword,
            @Param("shopId") Long shopId,
            Pageable pageable);


    @Query("SELECT p FROM Product p " +
            "LEFT JOIN p.categoryList c " +
            "WHERE p.isDelete = False " +
            "AND p.shopId = :shopId " +
            "GROUP BY p.id ")
    List<Product> getProductDataForExcel(Long shopId);


    @Modifying
    @Query("UPDATE Product p SET p.isDelete = true WHERE p.id = :id")
    void softDeleteProductByCategoryId(Long id);

    List<Product> findAllByShopId(Long id);

    Optional<Product> findById(Long id);

    @Query("SELECT COUNT(*) FROM Product p WHERE p.shopId = ?1")
    Long getQuantityByShopId(Long shopId);

    @Query("SELECT p FROM Product p WHERE p.id IN :productItemIds")
    List<Product> findAllByIds(@Param("productItemIds") List<Long> productItemIds);
}

