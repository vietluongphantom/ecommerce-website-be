package com.ptit.e_commerce_website_be.do_an_nhom.models.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class InventoryDTO {

    @JsonProperty("id")
    private Long id;

    @NotNull(message = "Quantity is required")
    @JsonProperty("quantity")
    private int quantity;

    @NotNull(message = "Product Item ID is required")
    @JsonProperty("product_item_id")
    private Long productItemId;

    @NotNull(message = "Warehouse ID is required")
    @JsonProperty("supplier_id")
    private Long supplierId;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}

