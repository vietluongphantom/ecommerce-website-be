package com.ptit.e_commerce_website_be.do_an_nhom.services;

import com.ptit.e_commerce_website_be.do_an_nhom.repositories.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Shop;
import java.util.Optional;

@Service
public class ShopService {

    private final ShopRepository shopRepository;

    public ShopService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    public Long getCurrentShopId(Long userId) {
        Optional<Shop> shop = shopRepository.findShopByUserId(userId);
        if (shop.isPresent()) {
            return shop.get().getId();
        }
        throw new RuntimeException("No shop found for the current user");
    }
}


