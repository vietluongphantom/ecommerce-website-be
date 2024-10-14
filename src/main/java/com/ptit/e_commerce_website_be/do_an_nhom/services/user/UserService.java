package com.ptit.e_commerce_website_be.do_an_nhom.services.user;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.LoginUserDto;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.RegisterUserDto;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.LoginResponse;

public interface UserService {
    User signUp(RegisterUserDto registerUserDto);
    LoginResponse authenticateUserAndGetLoginResponse(LoginUserDto loginUserDto);
    User getAuthenticatedUser();
    User getUserDetailsFromToken(String token);
}
