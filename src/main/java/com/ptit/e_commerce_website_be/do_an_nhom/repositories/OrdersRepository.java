package com.ptit.e_commerce_website_be.do_an_nhom.repositories;

import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Orders;
//import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {

    List<Orders> findByUserId(Long userId); // Thêm phương thức này

    List<Orders> findAllByUserId(Long userId);

//    @Query("SELECT o FROM Orders o WHERE o.shopId = ?1 AND o.id = ?2")
//    List<Orders> findByShopIdAndId(Long shopId, Long id);

    @Query("SELECT o FROM Orders o " +
            "WHERE o.shopId = :shopId " +
            "AND (:id IS NULL OR o.id = :id)")
    Page<Orders> findByShopIdAndId(
            @Param("shopId") Long shopId,
            Pageable pageable,
            @Param("id") Long id
    );


    @Query("SELECT COUNT(*) FROM Orders v WHERE v.shopId = ?1")
    Long getQuantityByShopId(Long shopId);


    @Query("SELECT o FROM Orders o WHERE o.shopId = ?1")
    List<Orders> findAll(Long shopId);
//    @Query("SELECT COUNT(*) FROM Orders o WHERE o.shopId = ?1")
//    Long getQuantityByShopId(Long shopId);

    List<Orders> findByIdIn(List<Long> ids);

    //    @Query("SELECT o FROM Orders o")
//    Page<Orders> findAllByAdmin(id, Pageable pageable );
    @Query("SELECT o FROM Orders o " +
            "WHERE (:id IS NULL OR o.id = :id)")
    Page<Orders> findAllByAdmin(
            @Param("id") Long id,
            Pageable pageable
    );

    /////
// Thống kê tổng doanh thu
    @Query("SELECT SUM(o.totalPrice) FROM Orders o WHERE o.status = 'COMPLETED'")
    BigDecimal getTotalRevenue();




    @Query("SELECT FUNCTION('MONTH', o.createdAt) as month, SUM(o.totalPrice) as revenue " +
            "FROM Orders o WHERE o.status = 'COMPLETED' AND FUNCTION('YEAR', o.createdAt) = :year " +
            "GROUP BY FUNCTION('MONTH', o.createdAt)")
    List<Object[]> getMonthlyRevenue(@Param("year") int year);


    @Query("SELECT FUNCTION('MONTH', o.createdAt) as month, SUM(o.totalPrice) as revenue " +
            "FROM Orders o WHERE o.status = 'COMPLETED' AND FUNCTION('YEAR', o.createdAt) = :year AND o.shopId = :shopId " +
            "GROUP BY FUNCTION('MONTH', o.createdAt)")
    List<Object[]> getMonthlyRevenueByShop(@Param("year") int year, @Param("shopId") Long shopId);

    @Query("SELECT DATE(o.createdAt) as date, SUM(o.totalPrice) as revenue " +
            "FROM Orders o " +
            "WHERE o.status = 'COMPLETED' AND FUNCTION('MONTH', o.createdAt) = :month AND FUNCTION('YEAR', o.createdAt) = :year " +
            "GROUP BY DATE(o.createdAt)")
    List<Object[]> getDailyRevenue(@Param("month") int month, @Param("year") int year);

    @Query("SELECT DATE(o.createdAt) as date, SUM(o.totalPrice) as revenue " +
            "FROM Orders o " +
            "WHERE o.status = 'COMPLETED' AND FUNCTION('MONTH', o.createdAt) = :month AND FUNCTION('YEAR', o.createdAt) = :year AND o.shopId = :shopId " +
            "GROUP BY DATE(o.createdAt)")
    List<Object[]> getDailyRevenueByShop(@Param("month") int month, @Param("year") int year, @Param("shopId") Long shopId);

    @Query("SELECT o.address FROM Orders o WHERE o.shopId = :shopId")
    List<String> findAddressDetailByShopId(@Param("shopId") Long shopId);

    List<Orders> findByStatusAndCreatedAtBetween(Orders.OrderStatus orderStatus, LocalDateTime startDate, LocalDateTime endDate);


    @Query("SELECT DATE(o.createdAt) AS date, SUM(o.totalPrice) AS revenue " +
            "FROM Orders o " +
            "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(o.createdAt) " +
            "ORDER BY DATE(o.createdAt)")
    List<Object[]> findRevenueByDateRange(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    @Query("SELECT DATE(o.createdAt) AS date, SUM(o.totalPrice) AS revenue " +
            "FROM Orders o " +
            "WHERE o.createdAt BETWEEN :startDate AND :endDate AND o.shopId = :shopId " +
            "GROUP BY DATE(o.createdAt) " +
            "ORDER BY DATE(o.createdAt)")
    List<Object[]> findRevenueByDateRangeAndShopId(@Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate,
                                                   @Param("shopId") Long shopId);
    Long countByShopIdAndStatus(Long shopId, Orders.OrderStatus status);

    Page<Orders> findByStatusAndUserId(Orders.OrderStatus status, Long userId,Pageable pageable);
    Page<Orders> findByStatusAndUserIdAndId(Orders.OrderStatus status, Long userId, Pageable pageable, Long id);
    //    List<Orders> findByUserIdAndId(Long userId, Long id);
    Page<Orders> findByUserId(Long userId, Pageable pageable);
    Page<Orders> findByUserIdAndId(Long userId, Long id, Pageable pageable);

    @Query("SELECT o FROM Orders o " +
            "WHERE o.shopId = :shopId " +
            "AND o.status = :status " +
            "AND (:id IS NULL OR o.id = :id)")
    Page<Orders> findByStatusWithPaginationBySeller(
            @Param("status") Orders.OrderStatus status,
            @Param("shopId") Long shopId,
            Pageable pageable,
            @Param("id") Long id
    );

    @Query("SELECT o FROM Orders o " +
            "WHERE o.status = :status " +
            "AND (:id IS NULL OR o.id = :id)")
    Page<Orders> findByStatusWithPaginationByAdmin(
            @Param("status") Orders.OrderStatus status,
            Pageable pageable,
            @Param("id") Long id
    );


}