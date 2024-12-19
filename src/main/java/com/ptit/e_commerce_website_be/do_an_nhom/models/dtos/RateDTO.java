package com.ptit.e_commerce_website_be.do_an_nhom.models.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RateDTO {
    private Long id;
    private BigDecimal averageStars;
    private Long quantity;
    private Long productId;
}
