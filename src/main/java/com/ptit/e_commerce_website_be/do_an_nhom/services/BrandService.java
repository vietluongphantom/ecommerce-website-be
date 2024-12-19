package com.ptit.e_commerce_website_be.do_an_nhom.services;

import com.ptit.e_commerce_website_be.do_an_nhom.repositories.BrandRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BrandService {

    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public List<Map<String, Object>> getBrandNamesWithProductCount() {
        List<Object[]> results = brandRepository.findBrandNamesWithProductCount();
        return results.stream()
                .map(result -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("brandName", result[0]);
                    map.put("productCount", result[1]);
                    return map;
                })
                .collect(Collectors.toList());
    }
}
