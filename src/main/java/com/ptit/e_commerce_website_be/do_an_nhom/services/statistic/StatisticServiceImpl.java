package com.ptit.e_commerce_website_be.do_an_nhom.services.statistic;

import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.DataNotFoundException;
import com.ptit.e_commerce_website_be.do_an_nhom.mapper.ProductMapper;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.ProductDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.StatisticDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.*;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductItemRepository productItemRepository;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public StatisticDTO getStatisticsOfCurrentShop(Long userId) {
        List<Orders> orders = ordersRepository.findAllByUserId(userId);

        BigDecimal totalSales = BigDecimal.ZERO;
        BigDecimal totalProfit = BigDecimal.ZERO;

        for (Orders order : orders) {
            if (order.getStatus() != Orders.OrderStatus.COMPLETED) {
                totalSales = totalSales.add(order.getTotalPrice());
                List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(order.getId());
                for (OrderItem orderItem : orderItems) {
                    ProductItem productItem = productItemRepository.findById(orderItem.getProductItemId())
                            .orElse(null);
                    if (productItem != null) {
                        BigDecimal itemProfit = productItem.getSalePrice().subtract(productItem.getPrice());
                        totalProfit = totalProfit.add(itemProfit.multiply(new BigDecimal(orderItem.getQuantity())));
                    }
                }
            }
        }

        return StatisticDTO.builder()
                .sales(totalSales)
                .profit(totalProfit)
                .build();
    }

    @Override
    public Page<ProductDTO> getAllProductsOfCurrentShop(Long userId, Pageable pageable) {
        Optional<Shop> shop = shopRepository.findShopByUserId(userId);
        if (shop.isEmpty()) {
            throw new DataNotFoundException("No shop found with this user");
        }
        List<Product> products = productRepository.findAllByShopId(shop.get().getId());
        return convertProductsListToPage(products, pageable)
                .map(productMapper::toDTO);
    }

    private Page<Product> convertProductsListToPage(List<Product> products, Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        List<Product> pagedProducts;

        if (products.size() < startItem) {
            pagedProducts = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, products.size());
            pagedProducts = products.subList(startItem, toIndex);
        }

        return new PageImpl<>(pagedProducts, pageable, products.size());
    }

    @Override
    public StatisticDTO getStatisticsOfAnProduct(Long userId, Long productId){
        Shop shop = shopRepository.findShopByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("No shop found with this user"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new DataNotFoundException("Product not found with this id"));

        if (!Objects.equals(shop.getId(), product.getShopId())) {
            throw new DataNotFoundException("Product not found in your shop");
        }

        BigDecimal totalSales = BigDecimal.ZERO;
        BigDecimal totalProfit = BigDecimal.ZERO;

        List<ProductItem> productItems = productItemRepository.findAllByProductId(product.getId());
        for (ProductItem productItem : productItems) {
            Optional<List<OrderItem>> orderItems = orderItemRepository.findAllByProductItemId(productItem.getId());
            List<OrderItem> currentOrderItems = orderItems.get();
            for (OrderItem orderItem : currentOrderItems) {
                Orders order = ordersRepository.findById(orderItem.getOrderId()).get();
                if (order.getStatus() == Orders.OrderStatus.COMPLETED) {
                    totalSales = totalSales.add(orderItem.getUnitPrice().multiply(new BigDecimal(orderItem.getQuantity())));
                    totalProfit = totalProfit.add(productItem.getSalePrice().subtract(productItem.getPrice()).multiply(new BigDecimal(orderItem.getQuantity())));
                }
            }
        }

        return StatisticDTO.builder()
                .sales(totalSales)
                .profit(totalProfit)
                .build();
    }
}
