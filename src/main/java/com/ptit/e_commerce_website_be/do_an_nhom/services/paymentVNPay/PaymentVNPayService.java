package com.ptit.e_commerce_website_be.do_an_nhom.services.paymentVNPay;


import com.ptit.e_commerce_website_be.do_an_nhom.configs.VNPayConfig;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.PaymentResponse;
import com.ptit.e_commerce_website_be.do_an_nhom.utils.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentVNPayService {
    private final VNPayConfig vnPayConfig;
    public PaymentResponse createVnPayPayment(long price, HttpServletRequest request, String listOrderId) {
        long amount = price * 100L;
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
//        if (bankCode != null && !bankCode.isEmpty()) {
//            vnpParamsMap.put("vnp_BankCode", bankCode);
//        }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        vnpParamsMap.put("vnp_ReturnUrl", vnPayConfig.getVnp_ReturnUrl() +"?listOrderId=" + listOrderId);
        //build query url
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        return PaymentResponse.builder()
                .status("ok")
                .message("success")
                .URL(paymentUrl)
                .build();
    }

}


