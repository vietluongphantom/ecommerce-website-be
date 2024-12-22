package com.ptit.e_commerce_website_be.do_an_nhom.controllers;


import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.DetailSupplierDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Supplier;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.CommonResult;
import com.ptit.e_commerce_website_be.do_an_nhom.services.supplier.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/supplier")
@RequiredArgsConstructor
public class SupplierController {
    private final SupplierService supplierService;

    @GetMapping("/{id}")
    public CommonResult<DetailSupplierDTO> getSupplierInfo(
            @PathVariable("id") Long id
    ){
        return CommonResult.success(supplierService.getSupplierInfo(id), "Get product attributes successfully");
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<DetailSupplierDTO> createSupplier (
            @Valid @RequestBody DetailSupplierDTO detailSupplierDTO
    ){
        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success(supplierService.createSupplier(detailSupplierDTO, user.getId()),"Create Supplier successfully");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<DetailSupplierDTO> updateSupplierById(
            @PathVariable Long id,
            @Valid @RequestBody DetailSupplierDTO detailSupplierDTO
    ){
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        supplierService.updateSupplierById(detailSupplierDTO,id, user.getId());
        return CommonResult.success(supplierService.updateSupplierById(detailSupplierDTO,id, user.getId()),"Update Supplier successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public  CommonResult deleteSupplier(@PathVariable Long id) {
        User user  = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        supplierService.deleteSupplierById(id, user.getId());
        return CommonResult.success("Delete Supplier successfully ");
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<Page<Supplier>> getAllSupplier(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "",required = false) String name
    ){
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("id").ascending());
        User user  = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success(supplierService.getAllSupplier(pageRequest,user.getId(), name), "Get all categories");
    }

}

