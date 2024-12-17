package com.ptit.e_commerce_website_be.do_an_nhom.controllers;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.DetailSellerInfoDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.DetailShopInfoDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Shop;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.CommonResult;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.ShopRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.services.seller.SellerService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.shop.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
//    @PreAuthorize("hasAnyRole('SELLER')")
    public CommonResult<DetailShopInfoDTO> getInformationShopById(
            @PathVariable Long id
    ){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return  CommonResult.success(shopService.getShopInfoById(id,user.getId()),"get information shop successfully");
    }
//
//    @GetMapping("/current-id")
//    @PreAuthorize("hasAnyRole('SELLER')")
//    public CommonResult<Long> getCurrentShopId() {
//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        Long shopId = shopService.getCurrentShopId(user.getId());
//        return CommonResult.success(shopId, "Get current shop ID successfully");
//    }
//
//    private final ShopRepository shopRepository;
//
//    @Autowired
//    public ShopController(ShopRepository shopRepository) {
//        this.shopRepository = shopRepository;
//    }
//
//    @GetMapping("/getShopId")
//    @PreAuthorize("hasAnyRole('SELLER')")
//    public ResponseEntity<?> getShopId() {
//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        Long userId = user.getId();
//
//        // Tìm thông tin Shop thông qua userId
//        Optional<Shop> shopOptional = shopRepository.findShopByUserId(userId);
//
//        if (shopOptional.isPresent()) {
//            Shop shop = shopOptional.get();
//            return ResponseEntity.ok("Found shopId: " + shop.getId());
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Shop not found for userId: " + userId);
//        }
//    }
}
//23h49 16/12