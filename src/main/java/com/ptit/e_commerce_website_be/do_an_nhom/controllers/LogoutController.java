package com.ptit.e_commerce_website_be.do_an_nhom.controllers;


import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.CommonResult;
import com.ptit.e_commerce_website_be.do_an_nhom.services.blacklisttoken.BlacklistTokenService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LogoutController {
    public final UserService userService;
    public final BlacklistTokenService blacklistTokenService;

//    @GetMapping("/loginAgain")
//    public CommonResult<Map<String, Object>> index(HttpServletRequest request) throws Exception{
//        System.out.println("qua lowp login again");
//        String token = null;
//        String authorizationHeader = request.getHeader("Authorization");
//        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
//            token = authorizationHeader.substring(7);
//            User userDetail = userService.getUserDetailsFromToken(token);
//            blacklistTokenService.addToBlackList(token, userDetail);
//        }
//        Map<String, Object> responseBody = new HashMap<>();
//        responseBody.put("message", "Good");
//        responseBody.put("status", HttpStatus.OK.value());
//        return CommonResult.success(responseBody);
//    }

    @GetMapping("/logout-admin")
    public CommonResult<Map<String, Object>> logoutAdmin(HttpServletRequest request) throws Exception{
        String token = null;
        String authorizationHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
            User userDetail = userService.getUserDetailsFromToken(token);
            blacklistTokenService.addToBlackList(token, userDetail);
        }
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "logout admin");
        responseBody.put("status", HttpStatus.OK.value());
        return CommonResult.success(responseBody);
    }

    @GetMapping("/logout-seller")
    public CommonResult<Map<String, Object>> logoutSeller(HttpServletRequest request) throws Exception{
        String token = null;
        String authorizationHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
            User userDetail = userService.getUserDetailsFromToken(token);
            blacklistTokenService.addToBlackList(token, userDetail);
        }
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "logout seller");
        responseBody.put("status", HttpStatus.OK.value());
        return CommonResult.success(responseBody);
    }

    @GetMapping("/logout-user")
    public CommonResult<Map<String, Object>> logoutUser(HttpServletRequest request) throws Exception{
        String token = null;
        String authorizationHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
            User userDetail = userService.getUserDetailsFromToken(token);
            blacklistTokenService.addToBlackList(token, userDetail);
        }
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "logout user");
        responseBody.put("status", HttpStatus.OK.value());
        return CommonResult.success(responseBody);
    }
}
