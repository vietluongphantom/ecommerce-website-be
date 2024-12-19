package com.ptit.e_commerce_website_be.do_an_nhom.services.statistic;

import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.DataNotFoundException;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.ProductDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.StatisticDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StatisticService {
    StatisticDTO getStatisticsOfCurrentShop(Long userId);

    StatisticDTO getStatisticsOfAnProduct(Long userId, Long productId);

    Page<ProductDTO> getAllProductsOfCurrentShop(Long userId, Pageable pageable);
}