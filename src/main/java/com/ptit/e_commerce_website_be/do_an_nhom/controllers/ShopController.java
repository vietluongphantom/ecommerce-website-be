package com.ptit.e_commerce_website_be.do_an_nhom.controllers;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.DetailSellerInfoDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.DetailShopInfoDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.CommonResult;
import com.ptit.e_commerce_website_be.do_an_nhom.services.seller.SellerService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.shop.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shop")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;
    @GetMapping
    @PreAuthorize("hasAnyRole('SELLER')")
    public CommonResult<DetailShopInfoDTO> getInformationShop() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return  CommonResult.success(shopService.getShopInfo(user.getId()),"get information shop successfully");
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('SELLER')")
    public CommonResult updateInformationShop(@Valid @RequestBody DetailShopInfoDTO detailShopInfoDTO){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return  CommonResult.success(shopService.updateShopInfo(detailShopInfoDTO,user.getId()),"update information shop successfully");
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SELLER')")
    public CommonResult<DetailShopInfoDTO> createInformationShop(
            @Valid @RequestBody DetailShopInfoDTO detailShopInfoDTO
    ){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success(shopService.createInformationShop(detailShopInfoDTO, user.getId()), "create information shop successfully");
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER')")
    public CommonResult<DetailShopInfoDTO> getInformationShopById(
            @PathVariable Long id
    ){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return  CommonResult.success(shopService.getShopInfoById(id,user.getId()),"get information shop successfully");
    }
}