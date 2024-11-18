package com.ptit.e_commerce_website_be.do_an_nhom.controllers;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.CartItemDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.request.ApplyVoucherDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.request.UpdateCartItemQuantityDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.CartItem;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.CommonResult;
import com.ptit.e_commerce_website_be.do_an_nhom.services.cart.ICartItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/cart-items")
public class CartItemController {

    private final ICartItemService cartItemService;

    @GetMapping("/{id}")
    public CommonResult<CartItem> getCartItemById(
            @PathVariable("id") Long cartItemId
    ){
        CartItem existingCartItem = cartItemService.getCartItemById(cartItemId);
        return CommonResult.success(existingCartItem, "Get cart item successfully");
    }

    @PostMapping("")
    public CommonResult<Object> createCartItem(
            @Valid @RequestBody CartItemDTO cartItemDTO
    ){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        cartItemService.createCartItem(cartItemDTO, user.getId());
        return CommonResult.success("Create cart item successfully");
    }

    @GetMapping("")
    public CommonResult<Page<CartItem>> getAllCartItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ){
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("id").ascending());
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Page<CartItem> cartItemPages = cartItemService.getAllCartItems(pageRequest, user.getId());
        return CommonResult.success(cartItemPages, "Get all cart items successfully");
    }

    @DeleteMapping("/{id}")
    public CommonResult<Object> deleteCartItem(@PathVariable Long id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        cartItemService.deleteCartItem(id, user.getId());
        return CommonResult.success("Delete cart item successfully");
    }

    @PutMapping("/update")
    public CommonResult<CartItem> updateCartItemQuantity(
            @Valid @RequestBody UpdateCartItemQuantityDTO updateCartItemQuantityDTO
    ){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CartItem cartItem = cartItemService.updateCartItemQuantity(updateCartItemQuantityDTO.getId(), updateCartItemQuantityDTO.getQuantity(), user.getId());
        return CommonResult.success(cartItem, "Update cart item quantity successfully");
    }

    @GetMapping("/quantity")
    public CommonResult<Long> getQuantityCartItem()
    {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long quantityCartItem = cartItemService.getQuantityCartItem(user.getId());
        return CommonResult.success(quantityCartItem, "get quantity success");
    }

    @PostMapping("/apply-voucher")
    public CommonResult<Object> applyVoucherToCartItem(
            @Valid @RequestBody ApplyVoucherDTO applyVoucherDTO
    ){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        cartItemService.applyVoucherToCartItem(applyVoucherDTO.getCartItemId(), applyVoucherDTO.getVoucherId(), user.getId());
        return CommonResult.success("Voucher applied successfully to cart item");
    }
}
