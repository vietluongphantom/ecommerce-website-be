package com.ptit.e_commerce_website_be.do_an_nhom.models.dtos;

import com.ptit.e_commerce_website_be.do_an_nhom.models.enums.Gender;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SellerRegisterDto extends RegisterUserDto {

    private String tax;

    private String cccd;

    private Long shopId;

//    @Builder(builderClassName = "SellerRegisterDtoBuilder")
    public SellerRegisterDto(String email, String password, String fullName, String phone, Gender gender, String tax, String cccd, Long shopId, String country, String province, String district, String commune, String addressDetail) {
        super(email, password, fullName, phone, gender, country, province, district, commune, addressDetail);
        this.tax = tax;
        this.cccd = cccd;
        this.shopId = shopId;
    }
}
