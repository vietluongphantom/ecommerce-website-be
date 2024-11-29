package com.ptit.e_commerce_website_be.do_an_nhom.controllers;


import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.OrdersDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.request.CheckoutRequestDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.CommonResult;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.PaymentResponse;
import com.ptit.e_commerce_website_be.do_an_nhom.services.checkout.ICheckoutService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.paymentVNPay.PaymentVNPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/checkout")
public class CheckoutController {

    private final ICheckoutService checkoutService;
    private final PaymentVNPayService paymentVNPayService;

    @PostMapping("/checkout_cart")
    public ResponseEntity<CommonResult<Map<String, String>>> checkout(@Valid @RequestBody CheckoutRequestDTO checkoutRequest,
                                                                      HttpServletRequest request){
        try {
            // Lấy thông tin người dùng từ SecurityContextHolder
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = user.getId(); // Lấy userId từ đối tượng User
            // Thực hiện checkout
            List<OrdersDTO> ordersDTO = checkoutService.checkoutCart(
                    userId, // Thay thế checkoutRequest.getUserId() bằng userId
                    checkoutRequest.isMethod(),
                    checkoutRequest.getNote(),
                    checkoutRequest.getSelectedCartItems()
            );

            BigDecimal totalPrice = BigDecimal.ZERO;
            String listOrderId = "";
            for (OrdersDTO orderItem : ordersDTO) {
                totalPrice = totalPrice.add(orderItem.getTotalPrice());
                listOrderId+= orderItem.getId();
            }


            // Gọi phương thức tạo phiên thanh toán sau khi tạo đơn hàng thành công
//            Map<String, Object> paymentSession = paymentService.createCheckoutSession(ordersDTO);
            PaymentResponse paymentSession = paymentVNPayService.createVnPayPayment(totalPrice.longValue(), request,listOrderId);
            // Lấy URL từ phiên thanh toán
            String checkoutUrl = (String) paymentSession.getURL();

            // Trả về URL cho client để thực hiện chuyển hướng
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("url", checkoutUrl);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CommonResult.success(responseBody, "Checkout successful, please proceed to payment"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(CommonResult.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }


    @GetMapping("/total-price")
    public CommonResult<BigDecimal> getTotalPrice(
            @RequestParam List<Long> selectedCartItems
    ) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BigDecimal totalPrice = checkoutService.calculateCartTotal(user.getId(), selectedCartItems);
        return CommonResult.success(totalPrice, "Total price calculated successfully");
    }
}
