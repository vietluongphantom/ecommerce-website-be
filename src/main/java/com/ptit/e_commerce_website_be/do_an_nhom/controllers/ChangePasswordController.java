package com.ptit.e_commerce_website_be.do_an_nhom.controllers;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.request.ChangePasswordDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.CommonResult;
import com.ptit.e_commerce_website_be.do_an_nhom.services.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/changePassword")
@RequiredArgsConstructor
public class ChangePasswordController {

    private final OtpService otpService;

    @PostMapping("/")
    public CommonResult<String> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        return otpService.changePasswordNewVersion(changePasswordDTO);
    }

}
