package com.ptit.e_commerce_website_be.do_an_nhom.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class StatisticDTO {
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("profit")
    private BigDecimal profit;

    @JsonProperty("sales")
    private BigDecimal sales;

    @Min(value=1, message = "Order ID is required")
    @JsonProperty("order_id")
    private Long orderId;

    @Min(value=1, message = "Shop ID is required")
    @JsonProperty("shop_id")
    private Long shopId;
}

