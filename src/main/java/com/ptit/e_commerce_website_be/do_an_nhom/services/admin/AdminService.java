package com.ptit.e_commerce_website_be.do_an_nhom.services.admin;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.LoginUserDto;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.LoginResponse;


public interface AdminService {
    LoginResponse authenticateAdminAndGetLoginResponse(LoginUserDto loginUserDto) ;

    User getAuthenticatedAdmin();
}
