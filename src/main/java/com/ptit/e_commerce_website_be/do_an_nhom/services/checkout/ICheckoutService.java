package com.ptit.e_commerce_website_be.do_an_nhom.services.checkout;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.OrdersDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Orders;

import java.math.BigDecimal;
import java.util.List;

public interface ICheckoutService {

    List<OrdersDTO> checkoutCart(Long userId, boolean method, String note, List<Long> selectedCartItems);

    BigDecimal calculateCartTotal(Long userId, List<Long> selectedCartItems);
    void setStatusOrder(Enum<Orders.OrderStatus> statusOrder, List<Long> listOrder);
}
