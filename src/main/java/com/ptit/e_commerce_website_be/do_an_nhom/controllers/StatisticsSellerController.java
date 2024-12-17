package com.ptit.e_commerce_website_be.do_an_nhom.controllers;

import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.DataNotFoundException;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.ProductDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.StatisticDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.responses.CommonResult;
import com.ptit.e_commerce_website_be.do_an_nhom.services.ShopService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.TokenService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.statistic.StatisticService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/seller/statistics")
@RequiredArgsConstructor
public class StatisticsSellerController {

    private final StatisticService statisticService;

    // An API (get overall statistics of current shop) which is not so good
    @GetMapping
//    @PreAuthorize("hasAnyRole('SELLER')")
    public CommonResult<StatisticDTO> getStatisticsOfCurrentShop() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success(statisticService.getStatisticsOfCurrentShop(user.getId()));
    }

    // Get all products of current shop, so when click on one, return the
    // statistics correspondingly
    @GetMapping("/products")
//    @PreAuthorize("hasRole('SELLER')")
    public CommonResult<Page<ProductDTO>> getAllProductsOfCurrentShop(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success(statisticService.getAllProductsOfCurrentShop(user.getId(), pageable));
    }

    @GetMapping("/products/{productId}")
//    @PreAuthorize("hasAnyRole('SELLER')")
    public CommonResult<StatisticDTO> getStatisticsOfAnProduct(@PathVariable Long productId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success(statisticService.getStatisticsOfAnProduct(user.getId(), productId));
    }

    private final TokenService tokenService;

    @GetMapping("/current-shop")
    public ResponseEntity<Map<String, Object>> getCurrentShop(HttpServletRequest request) {
        String token = extractToken(request);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Token không hợp lệ"));
        }

        try {
            Long userId = tokenService.getUserIdFromToken(token);
            Long shopId = tokenService.getShopIdByUserId(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("shopId", shopId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

}