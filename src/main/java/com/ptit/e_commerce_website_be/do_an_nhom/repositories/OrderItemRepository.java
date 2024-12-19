package com.ptit.e_commerce_website_be.do_an_nhom.repositories;

import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

//    static List<OrderItem> findByOrderId(Long orderId);
    List<OrderItem> findByOrderId(Long orderId);
    Optional<List<OrderItem>> findAllByProductItemId(Long id);

    List<OrderItem> findAllByOrderId(Long id);

    @Query("SELECT COUNT(oi) > 0 FROM OrderItem oi " +
            "JOIN ProductItem pi ON oi.productItemId = pi.id " +
            "JOIN Orders o ON oi.orderId = o.id " +
            "WHERE o.userId = :userId AND pi.productId = :productId")
    boolean hasUserPurchasedProduct(@Param("userId") Long userId, @Param("productId") Long productId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.orderId IN :orderIds")
    List<OrderItem> findByOrderIds(@Param("orderIds") List<Long> orderIds);


}