package com.ptit.e_commerce_website_be.do_an_nhom.services.auth;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.LoginUserDto;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.RegisterUserDto;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.LoginResponse;
import org.springframework.security.core.Authentication;

public interface AuthenticationService {
    Authentication getAuthentication();
    User authenticateByRole(LoginUserDto loginUserDto, String role);
    LoginResponse authenticateUserAndGetLoginResponse(LoginUserDto loginUserDto);
    LoginResponse buildLoginResponse(User authenticatedUser);
    boolean checkValidEmail(RegisterUserDto registerUserDto);
}
