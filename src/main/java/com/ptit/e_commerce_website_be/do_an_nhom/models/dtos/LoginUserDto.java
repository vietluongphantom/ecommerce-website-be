package com.ptit.e_commerce_website_be.do_an_nhom.models.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUserDto {
    private String email;

    private String password;
}
