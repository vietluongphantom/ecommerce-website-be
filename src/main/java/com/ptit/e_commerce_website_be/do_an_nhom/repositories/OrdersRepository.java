package com.ptit.e_commerce_website_be.do_an_nhom.repositories;

import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.OrderItem;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Orders;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {

    List<Orders> findByUserId(Long userId); // Thêm phương thức này

    List<Orders> findAllByUserId(Long userId);

    List<Orders> findAllByShopId(Long ShopId);

    @Query("SELECT COUNT(*) FROM Orders v WHERE v.shopId = ?1")
    Long getQuantityByShopId(Long shopId);


    @Query("SELECT o FROM Orders o WHERE o.shopId = ?1")
    List<Orders> findAll(Long shopId);
//    @Query("SELECT COUNT(*) FROM Orders o WHERE o.shopId = ?1")
//    Long getQuantityByShopId(Long shopId);

    List<Orders> findByIdIn(List<Long> ids);

    @Query("SELECT o FROM Orders o")
    List<Orders> findAllByAdmin();
}

    /////
// Thống kê tổng doanh thu
    @Query("SELECT SUM(o.totalPrice) FROM Orders o WHERE o.status = 'COMPLETED'")
    BigDecimal getTotalRevenue();


//    @Query("SELECT SUM(o.totalPrice) FROM Orders o WHERE o.status = 'COMPLETED' AND o.orderDate BETWEEN :startDate AND :endDate")
//    BigDecimal getTotalRevenueWithinPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Thống kê doanh thu theo ngày
//    @Query("SELECT DATE(o.createdAt) as date, SUM(o.totalPrice) as revenue " +
//            "FROM Orders o WHERE o.status = 'COMPLETED' GROUP BY DATE(o.createdAt)")
//    List<Object[]> getDailyRevenue();

//    @Query("SELECT SUM(o.totalPrice) FROM Orders o WHERE o.status = 'COMPLETED' AND o.orderDate BETWEEN :startDate AND :endDate")
//    BigDecimal getTotalRevenue(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);



     //Thống kê doanh thu theo tháng
//    @Query("SELECT FUNCTION('MONTH', o.createdAt) as month, SUM(o.totalPrice) as revenue " +
//            "FROM Orders o WHERE o.status = 'COMPLETED' GROUP BY FUNCTION('MONTH', o.createdAt)")
//    List<Object[]> getMonthlyRevenue();

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
}