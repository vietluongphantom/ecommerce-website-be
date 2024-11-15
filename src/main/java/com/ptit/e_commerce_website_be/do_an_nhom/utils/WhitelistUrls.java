package com.ptit.e_commerce_website_be.do_an_nhom.utils;

public class WhitelistUrls {
    public static final String[] URLS = {
            "/api/v1/user/**",
            "/api/v1/admin/login",
            "/api/v1/seller/**",
            "/api/v1/seller/signup",
            "/api/v1/seller/login",
    };
}