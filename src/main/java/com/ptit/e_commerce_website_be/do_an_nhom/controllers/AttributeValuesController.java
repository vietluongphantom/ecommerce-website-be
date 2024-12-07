package com.ptit.e_commerce_website_be.do_an_nhom.controllers;


import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.AttributeValuesDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.AttributeValues;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.CommonResult;
import com.ptit.e_commerce_website_be.do_an_nhom.services.attributeValues.AttributeValueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attribute-values")
public class AttributeValuesController {
    private final AttributeValueService attributeValueService;


    @PostMapping
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<String> createAttributeValues(
            @Valid @RequestBody AttributeValuesDTO attributeValuesDTO
    ){
        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        attributeValueService.createAttributeValues(attributeValuesDTO, user.getId());
        return CommonResult.success("Create attribute values successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<String> deleteAttributeValues(
            @PathVariable Long id
    ){
        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        attributeValueService.deleteAttributeValues(id, user.getId());
        return CommonResult.success("Delete attribute values successfully");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<List<AttributeValues>> getALLAttributeValues(
            @PathVariable Long id
    ){
        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success(attributeValueService.getALLAttributeValues(id, user.getId()),"Delete attribute values successfully");
    }

}

