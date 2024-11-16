package com.ptit.e_commerce_website_be.do_an_nhom.controllers;


import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.DetailWarehouseDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Warehouse;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.CommonResult;
import com.ptit.e_commerce_website_be.do_an_nhom.services.warehouse.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController {
    private final WarehouseService warehouseService;

    @GetMapping("/{id}")
    public CommonResult<DetailWarehouseDTO> getWarehouseInfo(
            @PathVariable("id") Long id
    ){
        return CommonResult.success(warehouseService.getWarehouseInfo(id), "Get product attributes successfully");
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<DetailWarehouseDTO> createWarehouse (
            @Valid @RequestBody  DetailWarehouseDTO detailWarehouseDTO
    ){
        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success(warehouseService.createWarehouse(detailWarehouseDTO, user.getId()),"Create warehouse successfully");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<DetailWarehouseDTO> updateWarehouseById(
            @PathVariable Long id,
            @Valid @RequestBody DetailWarehouseDTO detailWarehouseDTO
    ){
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        warehouseService.updateWarehouseById(detailWarehouseDTO,id, user.getId());
        return CommonResult.success(warehouseService.updateWarehouseById(detailWarehouseDTO,id, user.getId()),"Update warehouse successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public  CommonResult deleteWarehouse(@PathVariable Long id) {
        User user  = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        warehouseService.deleteWarehouseById(id, user.getId());
        return CommonResult.success("Delete warehouse successfully ");
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<Page<Warehouse>> getAllWarehouse(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "",required = false) String name
    ){
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("id").ascending());
        User user  = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success(warehouseService.getAllWarehouse(pageRequest,user.getId(), name), "Get all categories");
    }

}

