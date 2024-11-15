package com.ptit.e_commerce_website_be.do_an_nhom.repositories;


import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.DetailProductItemDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductItemRepository extends JpaRepository<ProductItem, Long> {


    @Query("SELECT pi FROM ProductItem pi WHERE pi.skuCode = ?1 AND pi.productId = ?2 ")
    ProductItem findBySkuCode(String skuCode, Long productId);

    @Query(name = "ProductItem.GetAllProductItemByProductId", nativeQuery = true)
    List<DetailProductItemDTO> findAllProductItemByProductId(@Param("product_id") Long productId);

    @Query(value = "SELECT * FROM Product_item pi " +
            "WHERE pi.product_id = ?1 "+
            "AND pi.is_delete = 0 "+
            "ORDER BY pi.id "
            , nativeQuery = true)
    List<ProductItem> findAllByProductId(Long productId);


    void deleteByProductId(Long productId);
    @Query(value = "SELECT p.shop_id FROM product_item pi JOIN product p ON pi.product_id = p.id WHERE pi.id = :productItemId", nativeQuery = true)
    Long findShopIdByProductItemId(@Param("productItemId") Long productItemId);

    @Modifying
    @Query("UPDATE ProductItem pi SET pi.isDelete = true WHERE pi.productId = :id")
    void softDeleteProductItemByProductId(Long id);

//    List<ProductItem> findAllByProductId(Long id);

    @Query("SELECT pi " +
            "FROM ProductItem pi " +
            "LEFT JOIN ProductItemAttributes pia ON pi.id = pia.productItemId "+
            "WHERE pia.attributeValueId IN :valuesIds " +
            "AND pi.productId = :id " +
            "AND pi.isDelete = false " +
            "GROUP BY pi.id " +
            "HAVING COUNT(DISTINCT pia.attributeValueId) = :valuesCount "
    )
    List<ProductItem> findProductItemByAttributesValues(@Param("id") Long id,@Param("valuesIds") List<Long> valuesIds,@Param("valuesCount") int valuesCount);


    @Query("SELECT SUM(subquery.quantity) " +
            "FROM (" +
            "    SELECT pi.id AS itemId, pi.quantity AS quantity " +
            "    FROM ProductItem pi " +
            "    LEFT JOIN ProductItemAttributes pia ON pi.id = pia.productItemId " +
            "    WHERE pia.attributeValueId IN :valuesIds " +
            "    AND pi.productId = :id " +
            "    GROUP BY pi.id " +
            "    HAVING COUNT(DISTINCT pia.attributeValueId) = :valuesCount " +
            ") AS subquery")
    Long sumQuantity(@Param("id") Long id,
                     @Param("valuesIds") List<Long> valuesIds,
                     @Param("valuesCount") int valuesCount);


    @Query("SELECT SUM(pi.quantity) FROM ProductItem pi WHERE pi.productId = :id")
    Long getQuantityProduct(@Param("id") Long id);

    Optional<ProductItem> findById(Long id);

    @Query("SELECT pi FROM ProductItem pi LEFT JOIN Product p ON p.shopId = ?2 WHERE pi.productId = ?1 AND pi.isDelete = false")
    List<ProductItem> getListProductItemByProductId(Long productId, Long shopId);
}

