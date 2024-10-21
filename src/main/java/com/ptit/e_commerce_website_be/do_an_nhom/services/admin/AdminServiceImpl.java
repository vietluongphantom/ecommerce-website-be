package com.ptit.e_commerce_website_be.do_an_nhom.services.admin;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.LoginUserDto;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.LoginResponse;
import com.ptit.e_commerce_website_be.do_an_nhom.services.auth.AuthenticationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{

    private final AuthenticationServiceImpl authenticationService;

    @Override
    public LoginResponse authenticateAdminAndGetLoginResponse(LoginUserDto loginUserDto){
        return authenticationService.authenticateAdminAndGetLoginResponse(loginUserDto);
    }

    @Override
//    @Cacheable(value = "admin")
    public User getAuthenticatedAdmin() {
        return (User) authenticationService.getAuthentication().getPrincipal();
    }
}
