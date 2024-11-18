package com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApplyVoucherDTO {
    private Long cartItemId;
    private Long voucherId;
}
