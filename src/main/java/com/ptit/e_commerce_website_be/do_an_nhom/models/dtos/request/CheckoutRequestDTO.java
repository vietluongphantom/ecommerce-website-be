package com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder

public class CheckoutRequestDTO {
    private boolean method;
    private String note;
    private List<Long> selectedCartItems; // Danh sách các ID của CartItem đã được chọn
    // Getter và Setter
}