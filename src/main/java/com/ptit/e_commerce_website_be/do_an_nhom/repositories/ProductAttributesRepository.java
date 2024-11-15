package com.ptit.e_commerce_website_be.do_an_nhom.repositories;

import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.ProductAttributes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductAttributesRepository extends JpaRepository<ProductAttributes, Long> {

    @Query(value = "SELECT * FROM product_attributes pa " +
            "WHERE pa.product_id = ?1 " +
            "AND pa.is_delete = 0 ", nativeQuery = true)
    List<ProductAttributes> findByProductId(Long id);

    @Modifying
    @Query("UPDATE ProductAttributes pa SET pa.isDelete = true WHERE pa.productId = :id")
    void softDeleteProductAttributesByProductId(@Param("id") Long id);

    @Query("SELECT pa FROM ProductAttributes pa WHERE pa.productId = :id AND pa.isDelete = false")
    List<ProductAttributes> findAllByProductId(@Param("id") Long id);

}