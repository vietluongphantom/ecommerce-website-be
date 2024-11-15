package com.ptit.e_commerce_website_be.do_an_nhom.models.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetailProductItemDTO {

    private Long id;

    @PositiveOrZero(message = "Price must be zero or positive")
    private BigDecimal price;

    private Integer quantity;

    private String name;

    @JsonProperty("sku_code")
    private String skuCode;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("import_price")
    private  BigDecimal importPrice;

    @JsonProperty("list_product_item")
    private List<ProductItemAttributesDTO> productItemAtrAttributesDTOS;

    @JsonProperty("image")
    private String image;

    @JsonProperty("total_sold")
    private Long totalSold;

}

