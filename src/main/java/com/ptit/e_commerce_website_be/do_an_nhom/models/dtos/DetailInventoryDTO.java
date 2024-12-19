package com.ptit.e_commerce_website_be.do_an_nhom.models.dtos;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@Setter
@Getter
public class DetailInventoryDTO {

    @JsonProperty("id")
    private Long id;

    @NotNull(message = "Quantity is required")
    @JsonProperty("quantity")
    private Integer quantity;

    @NotBlank(message = "Sku code is required")
    @JsonProperty("sku_code")
    private String skuCode;

    @JsonProperty("name")
    private String name;

//    @NotBlank(message = "warehouse is required")
//    private String warehouse;


    private String supplier;

    @JsonProperty("create_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime createAt;

    @JsonProperty("supplier_id")
    @NotNull(message = "supplier_id is required")
    private Long supplierId;

    @NotBlank(message = "location is required")
    private String location;

    @JsonProperty("is_delete")
    private Boolean isDelete;

    @JsonProperty("import_price")
    private BigDecimal importPrice;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("price")
    private BigDecimal price;
    public DetailInventoryDTO( Integer quantity, String skuCode, String name, String supplier) {
        this.quantity = quantity;
        this.skuCode = skuCode;
        this.name = name;
        this.supplier = supplier;
    }
    //inventory
    public DetailInventoryDTO(Long id, Integer quantity, String skuCode, String name, String supplier,
                              BigDecimal importPrice, LocalDateTime createAt, Long productId , BigDecimal price) {
        this.id = id;
        this.quantity = quantity;
        this.skuCode = skuCode;
        this.name = name;
        this.supplier = supplier;
//        this.warehouse = warehouse;
        this.importPrice = importPrice;
        this.createAt = createAt;
        this.productId = productId;
        this.price = price;
    }

    // supply
    public DetailInventoryDTO(Long id, Integer quantity, String skuCode, String name, String supplier, BigDecimal importPrice,
                              String location, LocalDateTime createAt, Boolean isDelete,
                              BigDecimal price) {
        this.id = id;
        this.quantity = quantity;
        this.skuCode = skuCode;
        this.name = name;
        this.supplier = supplier;
        this.importPrice = importPrice;
//        this.warehouse = warehouse;
        this.location = location;
        this.createAt = createAt;
        this.isDelete = isDelete;
        this.price = price;
        this.productId = productId;
    }

}

