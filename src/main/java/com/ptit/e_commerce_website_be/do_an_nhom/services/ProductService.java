package com.ptit.e_commerce_website_be.do_an_nhom.services;

import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Product;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAvailableProducts() {
        return productRepository.findAllAvailableProductsWithAttributes();
    }
}

