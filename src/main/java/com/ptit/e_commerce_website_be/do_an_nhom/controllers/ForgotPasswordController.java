package com.ptit.e_commerce_website_be.do_an_nhom.controllers;


import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.request.EmailDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.CommonResult;
import com.ptit.e_commerce_website_be.do_an_nhom.services.OtpService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.RedisOtpService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.user.UserService;
import com.ptit.e_commerce_website_be.do_an_nhom.utils.ChangePassword;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/forgotPassword")
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final OtpService otpService;
    private final UserService userService;
    private final RedisOtpService redisOtpService;

    @PostMapping("/verifyEmail")
    public CommonResult<String> verifyEmailNewVersion(@Valid @RequestBody EmailDTO emailDTO) {
        return CommonResult.success(userService.sendOtpForForgotPasswordRequest(emailDTO.getEmail()));
    }

    @PostMapping("/verifyOtp")
    public CommonResult<String> verifyOtpNewVersion(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        Integer otp = Integer.parseInt(requestBody.get("otp"));
        boolean isOtpValid = redisOtpService.verifyOtp(email, otp);
        if (!isOtpValid) { return CommonResult.failed("Invalid OTP"); }
        return CommonResult.success(email, "OTP is valid");
    }

    @PostMapping("/resendOtp")
    public CommonResult<String> resendOtp(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        return otpService.resendOtpForForgotPassword(email);
    }

    @PostMapping("/changePassword")
    public CommonResult<String> changePassword(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String password = requestBody.get("password");
        String confirmPassword = requestBody.get("confirm_password");
        return otpService.changePasswordForgotPasswordVersion(email, password, confirmPassword);
    }

    @PostMapping("/verifyEmail/{email}")
    public CommonResult<String> verifyEmail(@PathVariable String email) {
        return otpService.verifyEmailAndSendOtpNewVersion(email);
    }

    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(@PathVariable Integer otp, @PathVariable String email) {
        return otpService.verifyOtp(otp, email);
    }

    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePasswordHandler(@Valid @RequestBody ChangePassword changePassword,
                                                        @PathVariable String email) {
        return otpService.changePassword(changePassword, email);
    }

    @PostMapping("/resendOtp/{email}")
    public ResponseEntity<String> resendOtp(@PathVariable String email) {
        return otpService.resendOtp(email);
    }
}
