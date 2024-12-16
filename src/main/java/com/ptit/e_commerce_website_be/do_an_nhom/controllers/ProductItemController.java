package com.ptit.e_commerce_website_be.do_an_nhom.controllers;


import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.DetailProductItemDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.ProductItem;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.CommonResult;
import com.ptit.e_commerce_website_be.do_an_nhom.services.productitem.ProductItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/sku")
@RequiredArgsConstructor
public class ProductItemController {

    private final ProductItemService productItemService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<DetailProductItemDTO> createProductItem(
            @Valid @RequestBody DetailProductItemDTO detailProductItemDTO
    ){
        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success(productItemService.createProductItem(detailProductItemDTO, user.getId()), "create product item successfully");
    }

    @GetMapping("/list-sku/{id}")
//    @PreAuthorize("hasRole('ROLE_SELLER')")
    public  CommonResult<Page<Object>> getAllProductItem(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @PathVariable Long id
    ){
        Pageable pageable = PageRequest.of(page, limit);
        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success(productItemService.getAllProductItem(id,user.getId(), pageable),"get all product item successfully");
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<DetailProductItemDTO> updateProductItem(
            @Valid @RequestBody DetailProductItemDTO detailProductItemDTO
    ){
        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success(productItemService.updateProductItem(detailProductItemDTO,user.getId()),"update product item successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<String> deleteProductItem(
            @PathVariable Long id
    ){
        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        productItemService.deleteProductItem(id, user.getId());
        return CommonResult.success("Delete product item id = " + id + " successfully");

    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('ROLE_SELLER')")
    public  CommonResult<DetailProductItemDTO> getProductItemById(
            @PathVariable Long id
    ){
        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success(productItemService.getProductItemById(id,user.getId()),"get product item successfully");
    }


    @GetMapping("/values/ok/{id}")
    public CommonResult<Map<String, Object>> getProductItemByAttributesValues(
            @PathVariable Long id,
            @RequestParam(defaultValue = "", name = "values-ids") List<Long> valuesIds
    ){
        return CommonResult.success( productItemService.getProductItemByAttributesValues(id, valuesIds),"Get quantity by attributes successfully");
    }

    @GetMapping("/product/{id}")
    public CommonResult<List<ProductItem>> getListProductItemByProductId(
            @PathVariable Long id
    ){
        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success(productItemService.getListProductItemByProductId(id, user.getId()),"Get list product item by product id successfully");
    }

}
