package com.ptit.e_commerce_website_be.do_an_nhom.repositories;

import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.OrderStatusHistory;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {
    List<OrderStatusHistory> findByOrderId(Long orderId);
    @Query("SELECT COUNT(o) FROM Orders o WHERE o.status = 'CANCELLED'")
    Long countCancelledOrders();

    @Query("SELECT COUNT(o) FROM Orders o WHERE o.status = 'COMPLETED'")
    Long countCompletedOrders();

    @Query("SELECT COUNT(o) FROM Orders o WHERE o.status = 'PENDING'")
    Long countPendingOrders();

    @Query("SELECT COUNT(o) FROM Orders o WHERE o.status = 'PACKED'")
    Long countPackedOrders();

    @Query("SELECT COUNT(o) FROM Orders o WHERE o.status = 'SHIPPED'")
    Long countShippedOrders();

    @Query("SELECT COUNT(o) FROM Orders o WHERE o.status = 'RETURNED'")
    Long countReturnedOrders();

    @Query("SELECT COUNT(o) FROM Orders o WHERE o.status = 'CONFIRMED'")
    Long countConfirmedOrders();



    @Query("SELECT osh.orderId FROM OrderStatusHistory osh WHERE osh.status = :status AND osh.changedAt BETWEEN :startDate AND :endDate")
    List<Long> findCompletedOrderIds(@Param("status") Orders.OrderStatus status,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);
}
