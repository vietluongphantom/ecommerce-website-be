package com.ptit.e_commerce_website_be.do_an_nhom.models.dtos;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDTO {

    private Long id;

    private Long orderId;

    private Long productItemId;

    private int quantity;
    private Long shopId;
    private BigDecimal unitPrice;

    private Long voucherId;



}
