package com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO để nhận chỉ số lượng
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCartItemQuantityDTO {

    private Long id;
    private int quantity;
}
