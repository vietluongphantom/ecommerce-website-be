package com.ptit.e_commerce_website_be.do_an_nhom.models.response;

import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Shop;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopResponse {
    User user;
    Shop shop;
}
