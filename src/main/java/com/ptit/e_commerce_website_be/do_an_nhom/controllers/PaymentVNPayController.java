package com.ptit.e_commerce_website_be.do_an_nhom.controllers;


import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Orders;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.PaymentResponse;
import com.ptit.e_commerce_website_be.do_an_nhom.services.checkout.ICheckoutService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.paymentVNPay.PaymentVNPayService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.product.IProductService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.productitem.ProductItemService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/payments")
@RequiredArgsConstructor
public class PaymentVNPayController {
    private final
    PaymentVNPayService paymentService;
    private final ICheckoutService checkoutService;
    private final ProductItemService productItemService;
//        @GetMapping("/create-payment")
//    public ResponseEntity<PaymentResponse> pay(
//            HttpServletRequest request,
//            @RequestParam("amount") Long amount
//
//    ) {
//        return ResponseEntity.ok(paymentService.createVnPayPayment(amount, request));
//    }
    @GetMapping("/vn-pay-callback")
    public ResponseEntity<PaymentResponse> payCallbackHandler(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam("listOrderId") List<Long> listOrderId
    ) throws IOException {
//        String listOrderId = request.getParameter("vnp_ResponseCode");
        String status = request.getParameter("vnp_ResponseCode");
        response.sendRedirect("http://localhost:5173/user/list-order?vnp_ResponseCode=" + status);
        if (status.equals("00")) {
            checkoutService.setStatusOrder(Orders.OrderStatus.CONFIRMED, listOrderId);
            return ResponseEntity.ok(PaymentResponse
                    .builder()
                    .status("00")
                    .message("Successfully")
                    .build());
        } else {
            checkoutService.setStatusOrder(Orders.OrderStatus.CANCELLED, listOrderId);
            productItemService.rollbackQuantity(listOrderId);
            return ResponseEntity.badRequest().body(
                    PaymentResponse
                            .builder()
                            .status("99")
                            .message("Failed")
                            .build()
            );
        }
    }

//
//    @GetMapping("/create-payment")
//    public ResponseEntity<String> createPayment(
//            @RequestParam("amount") Long amount,
//            @RequestParam("orderId") String orderId) {
//        String returnUrl = "http://localhost:3000/api/payments/vn-pay-callback";
//        String token = paymentService.createVnPayToken(amount, orderId, returnUrl);
//
//        // Tạo URL chuyển hướng
//        String paymentUrl = "https://sandbox.vnpayment.vn/paymentv2/Transaction/PaymentMethod.html?token=" + token;
//
//        // Trả về URL để FE chuyển hướng
//        return ResponseEntity.ok(paymentUrl);
//    }
}


