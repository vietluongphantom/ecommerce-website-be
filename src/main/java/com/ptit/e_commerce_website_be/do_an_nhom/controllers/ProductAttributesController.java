package com.ptit.e_commerce_website_be.do_an_nhom.controllers;


import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.ProductAttributesDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.ProductAttributes;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.CommonResult;
import com.ptit.e_commerce_website_be.do_an_nhom.services.productAttributes.ProductAttributesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product-attribute")
@RequiredArgsConstructor
public class ProductAttributesController {
    private final ProductAttributesService productAttributesService;

    @GetMapping("/{id}")
    public CommonResult<ProductAttributesDTO> getProductAttributesById(
            @PathVariable("id") Long IdProductAttributes
    ){
        return CommonResult.success(productAttributesService.getProductAttributesById(IdProductAttributes), "Get product attributes successfully");
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<Object> createProductAttributes (
            @PathVariable Long id,
            @Valid @RequestBody ProductAttributesDTO productAttributesDTO
    ){
        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success( productAttributesService.createProductAttributes(productAttributesDTO, id , user.getId()),"Create product attribute successfully");
    }
    @PutMapping
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<ProductAttributesDTO> updateProductAttributes(
            @Valid @RequestBody ProductAttributesDTO productAttributesDTO
    ){
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success(productAttributesService.updateProductAttributes( productAttributesDTO, user.getId()), "Update product attribute successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<ProductAttributesDTO> deleteProductAttributes(
            @PathVariable Long id
    ){
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success(productAttributesService.deleteProductAttributes( id, user.getId()), "Delete product attribute successfully");
    }

    @GetMapping("/all-product-attribute/{idProduct}")
    public CommonResult<List<ProductAttributes>> getProductAttributes(
            @PathVariable Long idProduct
    ) {
        return CommonResult.success(productAttributesService.getAllProductAttributes(idProduct), "Get all product attribute");
    }
}
