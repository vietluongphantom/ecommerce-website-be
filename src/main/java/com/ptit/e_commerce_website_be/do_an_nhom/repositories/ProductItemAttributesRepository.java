package com.ptit.e_commerce_website_be.do_an_nhom.repositories;

import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.ProductItemAttributes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductItemAttributesRepository extends JpaRepository<ProductItemAttributes, Long> {

    @Modifying
    @Query("DELETE FROM ProductItemAttributes p WHERE p.productItemId = :id")
    void deleteProductItemAttributesValue(@Param("id") Long id);
    List<ProductItemAttributes> findByProductItemId(Long productItemId);
//    ProductItemAttributes findByAttributeValueIdAndProductItemId
}

