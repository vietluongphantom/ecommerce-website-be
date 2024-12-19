package com.ptit.e_commerce_website_be.do_an_nhom.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueAndProfitResponse {
    private BigDecimal totalRevenue;
    private BigDecimal totalProfit;
}

