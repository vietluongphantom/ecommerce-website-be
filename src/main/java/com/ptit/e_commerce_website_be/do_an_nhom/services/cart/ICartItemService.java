package com.ptit.e_commerce_website_be.do_an_nhom.services.cart;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.CartItemDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;


public interface ICartItemService {
    CartItem getCartItemById(Long id);
    void createCartItem(CartItemDTO cartItemDTO, Long userId);
    Page<CartItem> getAllCartItems(PageRequest pageRequest, Long userId);
    void deleteCartItem(Long id, Long userId);
    CartItem updateCartItemQuantity(Long cartItemId, int quantity, Long userId);
    Long getQuantityCartItem(Long userId);
    void applyVoucherToCartItem(Long cartItemId, Long voucherId, Long userId);

}