package com.ptit.e_commerce_website_be.do_an_nhom.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ChangePassword(
        @JsonProperty("password") String password,
        @JsonProperty("repeat_password") String repeatPassword) {
}
