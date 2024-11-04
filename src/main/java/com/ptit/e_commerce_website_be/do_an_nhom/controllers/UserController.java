package com.ptit.e_commerce_website_be.do_an_nhom.controllers;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.RefreshTokenDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Token;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.LoginUserDto;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.RegisterUserDto;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.CommonResult;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.LoginResponse;
import com.ptit.e_commerce_website_be.do_an_nhom.services.token.TokenService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final TokenService tokenService;
    private final UserService userService;

    @PostMapping("/signUpNewVersion")
    public CommonResult<User> signup(@Valid @RequestBody RegisterUserDto registerUserDto){
        User user = userService.signUp(registerUserDto);
        System.out.println("OK");
        return CommonResult.success(user);
    }

    @PostMapping("/login")
    public CommonResult<LoginResponse> authenticate(@Valid @RequestBody LoginUserDto loginUserDto) {
        String token = userService.authenticateUserAndGetLoginResponse(loginUserDto).getToken();
        User userDetail = userService.getUserDetailsFromToken(token);
        Token jwtToken = tokenService.addToken(userDetail, token);

        LoginResponse loginResponse = LoginResponse.builder()
                .token(jwtToken.getToken())
                .tokenType(jwtToken.getTokenType())
                .refreshToken(jwtToken.getRefreshToken())
                .build();
        return CommonResult.success(loginResponse);
    }

    @PostMapping("/refreshToken")
    public CommonResult<LoginResponse> refreshToken(
            @Valid @RequestBody RefreshTokenDTO refreshTokenDTO
    ){

        User userDetail = userService.getUserDetailsFromRefreshToken(refreshTokenDTO.getRefreshToken());
        Token jwtToken = tokenService.refreshToken(refreshTokenDTO.getRefreshToken(), userDetail);
        LoginResponse loginResponse = LoginResponse.builder()
                .message("Refresh token successfully")
                .token(jwtToken.getToken())
                .tokenType(jwtToken.getTokenType())
                .refreshToken(jwtToken.getRefreshToken())
                .build();
        return CommonResult.success(loginResponse);
    }

}
