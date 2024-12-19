package com.ptit.e_commerce_website_be.do_an_nhom.repositories;

import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Image;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<OrderStatusHistory, Long> {
//    @Query("SELECT COUNT(*) FROM OrderStatusHistory o WHERE o.userId = ?1 AND isRead = NULL")
//    Long getQuantityNewNotification(Long userId);

    @Query(nativeQuery = true, value ="SELECT COUNT(*) " +
            "FROM order_status_history " +
            "WHERE user_id = ?1 AND is_read = 0;")
    Long getQuantityNewNotification(Long userId);
}
