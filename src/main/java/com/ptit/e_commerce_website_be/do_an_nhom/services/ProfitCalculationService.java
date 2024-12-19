package com.ptit.e_commerce_website_be.do_an_nhom.services;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.RevenueAndProfitResponse;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.OrderItem;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Orders;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.ProductItem;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.OrderItemRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.OrderStatusHistoryRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.ProductItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfitCalculationService {

    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductItemRepository productItemRepository;

    public RevenueAndProfitResponse calculateRevenueAndProfit(LocalDateTime startDate, LocalDateTime endDate) {
        // Lấy danh sách orderId đã hoàn thành
        List<Long> completedOrderIds = orderStatusHistoryRepository.findCompletedOrderIds(
                Orders.OrderStatus.COMPLETED, startDate, endDate);

        if (completedOrderIds.isEmpty()) {
            return new RevenueAndProfitResponse(BigDecimal.ZERO, BigDecimal.ZERO); // Không có đơn hàng hoàn thành
        }

        // Lấy danh sách OrderItem theo các orderId
        List<OrderItem> orderItems = orderItemRepository.findByOrderIds(completedOrderIds);

        // Lấy danh sách productItemId từ OrderItem
        List<Long> productItemIds = orderItems.stream()
                .map(OrderItem::getProductItemId)
                .distinct()
                .toList();

        // Lấy thông tin ProductItem
        Map<Long, ProductItem> productItemMap = productItemRepository.findByProductItemIds(productItemIds)
                .stream()
                .collect(Collectors.toMap(ProductItem::getId, item -> item));

        // Tính tổng doanh thu và lợi nhuận
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalProfit = BigDecimal.ZERO;

        for (OrderItem orderItem : orderItems) {
            ProductItem product = productItemMap.get(orderItem.getProductItemId());
            if (product != null) {
                BigDecimal revenue = orderItem.getUnitPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
                BigDecimal cost = product.getImportPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
                BigDecimal profit = revenue.subtract(cost);

                totalRevenue = totalRevenue.add(revenue);
                totalProfit = totalProfit.add(profit);
            }
        }

        return new RevenueAndProfitResponse(totalRevenue, totalProfit);
    }

    public RevenueAndProfitResponse calculateRevenueAndProfitByShop(LocalDateTime startDate, LocalDateTime endDate, Long shopId) {
        // Lấy danh sách orderId đã hoàn thành
        List<Long> completedOrderIds = orderStatusHistoryRepository.findCompletedOrderIds(
                Orders.OrderStatus.COMPLETED, startDate, endDate);

        if (completedOrderIds.isEmpty()) {
            return new RevenueAndProfitResponse(BigDecimal.ZERO, BigDecimal.ZERO); // Không có đơn hàng hoàn thành
        }

        // Lấy danh sách OrderItem theo các orderId
        List<OrderItem> orderItems = orderItemRepository.findByOrderIds(completedOrderIds);

        // Lấy danh sách productItemId từ OrderItem
        List<Long> productItemIds = orderItems.stream()
                .map(OrderItem::getProductItemId)
                .distinct()
                .toList();

        // Lấy thông tin ProductItem và lọc theo shopId
        Map<Long, ProductItem> productItemMap = productItemRepository.findByProductItemIds(productItemIds)
                .stream()
                .filter(item -> shopId.equals(productItemRepository.findShopIdByProductItemId2(item.getId())))
                .collect(Collectors.toMap(ProductItem::getId, item -> item));

        // Tính tổng doanh thu và lợi nhuận
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalProfit = BigDecimal.ZERO;

        for (OrderItem orderItem : orderItems) {
            ProductItem product = productItemMap.get(orderItem.getProductItemId());
            if (product != null) {
                BigDecimal revenue = orderItem.getUnitPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
                BigDecimal cost = product.getImportPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
                BigDecimal profit = revenue.subtract(cost);

                totalRevenue = totalRevenue.add(revenue);
                totalProfit = totalProfit.add(profit);
            }
        }

        return new RevenueAndProfitResponse(totalRevenue, totalProfit);
    }

}


//@Service
//@RequiredArgsConstructor
//public class ProfitCalculationService {
//
//    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
//    private final OrderItemRepository orderItemRepository;
//    private final ProductItemRepository productItemRepository;
//
//    public BigDecimal calculateProfit(LocalDateTime startDate, LocalDateTime endDate) {
//        // Lấy danh sách orderId đã hoàn thành
//        List<Long> completedOrderIds = orderStatusHistoryRepository.findCompletedOrderIds(
//                Orders.OrderStatus.COMPLETED, startDate, endDate);
//
//        if (completedOrderIds.isEmpty()) {
//            return BigDecimal.ZERO; // Không có đơn hàng hoàn thành
//        }
//
//        // Lấy danh sách OrderItem theo các orderId
//        List<OrderItem> orderItems = orderItemRepository.findByOrderIds(completedOrderIds);
//
//        // Lấy danh sách productItemId từ OrderItem
//        List<Long> productItemIds = orderItems.stream()
//                .map(OrderItem::getProductItemId)
//                .distinct()
//                .toList();
//
//        // Lấy thông tin ProductItem
//        Map<Long, ProductItem> productItemMap = productItemRepository.findByProductItemIds(productItemIds)
//                .stream()
//                .collect(Collectors.toMap(ProductItem::getId, item -> item));
//
//        // Tính tổng lợi nhuận
//        return orderItems.stream()
//                .map(orderItem -> {
//                    ProductItem product = productItemMap.get(orderItem.getProductItemId());
//                    if (product != null) {
//                        BigDecimal revenue = orderItem.getUnitPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
//                        BigDecimal cost = product.getImportPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
//                        return revenue.subtract(cost);
//                    }
//                    return BigDecimal.ZERO;
//                })
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//    }
//}

