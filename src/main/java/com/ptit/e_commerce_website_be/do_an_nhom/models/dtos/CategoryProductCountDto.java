package com.ptit.e_commerce_website_be.do_an_nhom.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryProductCountDto {
    private String categoryName;
    private Long productCount;
}