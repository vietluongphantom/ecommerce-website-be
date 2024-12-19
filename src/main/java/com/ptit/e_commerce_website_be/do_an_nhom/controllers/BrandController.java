package com.ptit.e_commerce_website_be.do_an_nhom.controllers;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.BrandDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Brand;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.CommonResult;
import com.ptit.e_commerce_website_be.do_an_nhom.services.BrandService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.brand.IBrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/brands")
public class BrandController {
    private final IBrandService brandService;
    private final BrandService brandService2;

    @GetMapping("/{id}")
    public CommonResult<Brand> getBrandById(
            @PathVariable("id") Long brandId
    ){
        Brand existingBrand = brandService.getBrandById(brandId);
        return CommonResult.success(existingBrand, "Get brand successfully");
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public CommonResult<Object> createBrand (
            @Valid @RequestBody BrandDTO brandDTO
    ){
        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Brand brand = brandService.createBrand(brandDTO, user.getId());
        return CommonResult.success(brand,"Create brand successfully");
    }

    @GetMapping("")
    public CommonResult<Page<Brand>> getAllBrands(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "",required = false) String name
    ) {
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("id").ascending());
        Page<Brand> brandsPages = brandService.getAllBrands(pageRequest, name);
        return CommonResult.success(brandsPages, "Get all brands");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public  CommonResult deleteBrand(@PathVariable Long id) {
        User user  = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        brandService.deleteBrand(id, user.getId());
        return CommonResult.success("Delete success brand");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public CommonResult<Brand> updateBrand(
            @PathVariable Long id,
            @Valid @RequestBody BrandDTO brandDTO
    ){
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Brand brand = brandService.updateBrand(id, brandDTO, user.getId());
        return CommonResult.success(brand, "Update brand successfully");
    }

//    public BrandController(BrandService brandService) {
//        this.brandService2 = brandService2;
//    }

    @GetMapping("/product-count")
    public ResponseEntity<List<Map<String, Object>>> getBrandNamesWithProductCount() {
        return ResponseEntity.ok(brandService2.getBrandNamesWithProductCount());
    }
}
