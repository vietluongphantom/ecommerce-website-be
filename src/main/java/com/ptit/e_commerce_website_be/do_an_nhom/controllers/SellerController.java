package com.ptit.e_commerce_website_be.do_an_nhom.controllers;

import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.DataNotFoundException;
import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.UserAlreadyExistedException;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.*;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Seller;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Shop;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.CommonResult;


import com.ptit.e_commerce_website_be.do_an_nhom.services.seller.SellerService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.SellerAlreadyExistedException;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/seller")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    private final UserService userService;


    @PostMapping("/signUpNewVersion")
    public CommonResult<User> signUpNewestVersion(@Valid @RequestBody RegisterUserDto registerUserDto) {
        return CommonResult.success(sellerService.signUpNewestVersion(registerUserDto));
    }


    // New version here

    // New version here

    @PostMapping("/login")
    public CommonResult<LoginResponse> authenticate(@Valid @RequestBody LoginUserDto loginUserDto){
        return CommonResult.success(sellerService.authenticateSellerAndGetLoginResponse(loginUserDto));
    }

    @PreAuthorize("hasRole('SELLER')")
    @PostMapping("/addShopInfo")
    public CommonResult<Shop> updateShopInfoAfterLogin(@Valid @RequestBody ShopDTO shopDTO){
        User authenticatedSeller = sellerService.getAuthenticatedSeller();
        return CommonResult.success(sellerService.updateShopInfo(authenticatedSeller.getId(), shopDTO));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('SELLER')")
    public CommonResult<User> authenticatedSeller() {
        return CommonResult.success(sellerService.getAuthenticatedSeller());
    }

    @GetMapping("/information")
    @PreAuthorize("hasAnyRole('SELLER')")
    public CommonResult<DetailSellerInfoDTO> getInformationSeller() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return  CommonResult.success(sellerService.getSellerInfo(user.getId()),"get information seller successfully");
    }

    @PutMapping("/information")
    @PreAuthorize("hasAnyRole('SELLER')")
    public CommonResult updateInformationSeller(@Valid @RequestBody DetailSellerInfoDTO detailSellerInfoDTO){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return  CommonResult.success(sellerService.updateSellerInfo(detailSellerInfoDTO, user.getId()),"get information seller successfully");
    }

    @GetMapping("/basicInfor")
    @PreAuthorize("hasAnyRole('ROLE_SELLER')")
    public  CommonResult<Map<String, Long>> getBasicInfor() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Long> result = new HashMap<>();
        result = sellerService.getBasicInfo(user.getId());
        return CommonResult.success(result,"get basic information success");
    }
}
