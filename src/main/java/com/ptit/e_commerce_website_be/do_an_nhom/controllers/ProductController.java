package com.ptit.e_commerce_website_be.do_an_nhom.controllers;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.CategoryProductCountDto;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Product;
import com.ptit.e_commerce_website_be.do_an_nhom.services.CategoryService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.ProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/available")
    public ResponseEntity<List<Map<String, Object>>> getAvailableProducts() {
        List<Product> products = productService.getAvailableProducts();

        // Map the Product objects to JSON format without created_at and modified_at
        List<Map<String, Object>> response = products.stream().map(product -> {
            Map<String, Object> productMap = new HashMap<>();
            productMap.put("id", product.getId());
            productMap.put("description", product.getDescription());
            productMap.put("name", product.getName());
            productMap.put("slug", product.getSlug());
            productMap.put("status", product.getStatus());
            productMap.put("total_sold", product.getTotalSold());
            productMap.put("product_view", product.getProductView());
            productMap.put("brand_id", product.getBrandId());
            productMap.put("shop_id", product.getShopId());
            productMap.put("min_price", product.getMinPrice());
            productMap.put("is_delete", product.getIsDelete());
            productMap.put("thumbnail", product.getThumbnail());
            return productMap;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    private final CategoryService categoryService;

    @GetMapping("category/product-count")
    public ResponseEntity<List<CategoryProductCountDto>> getCategoryProductCounts() {
        List<CategoryProductCountDto> categoryCounts = categoryService.getCategoryProductCounts();
        return ResponseEntity.ok(categoryCounts);
    }
}



//@RestController
//@RequestMapping("/api/products")
//@RequiredArgsConstructor
//public class ProductController {
//
//    private final ProductService productService;
//
//    @GetMapping("/available")
//    public ResponseEntity<List<Product>> getAvailableProducts() {
//        List<Product> products = productService.getAvailableProducts();
//        return ResponseEntity.ok(products);
//    }
//}

