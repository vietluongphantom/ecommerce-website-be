package com.ptit.e_commerce_website_be.do_an_nhom.services.orders;

import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.DataNotFoundException;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.OrderItemDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.OrdersDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IOrdersService {
    OrdersDTO addOrder(OrdersDTO orderDTO, Long userId);

    List<Orders> findAll(Long userId);

    Optional<Orders> findById(Long id);

    Orders save(Orders order);

    void deleteById(Long id);

    List<Orders> findByUserId(Long userId); // Thêm phương thức này

    void updateOrderStatus(Long orderId, Orders.OrderStatus newStatus);

    List<OrderItem> getOrderItems(Long orderId);

    List<Orders> getAllOrderBySeller(Long userId) ;

    List<OrderStatusHistory> getOrderHistory(Long orderId);

    boolean checkUserPurchasedProduct(Long userId, Long productId);
//    List<OrderItemDTO> findOrderItemsByUserId(Long userId);
    List<Orders> findAllForAdmin();

}
