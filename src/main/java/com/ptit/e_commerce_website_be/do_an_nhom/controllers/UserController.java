package com.ptit.e_commerce_website_be.do_an_nhom.controllers;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.RegisterUserDto;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.CommonResult;
import com.ptit.e_commerce_website_be.do_an_nhom.services.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("")
    public CommonResult<User> signup( @RequestBody RegisterUserDto registerUserDto){
        System.out.println("loi aaaaaaaaaaaaaaaaaaaaaaa");
        User user = userService.signUp(registerUserDto);
        return CommonResult.success(user);
    }
}
