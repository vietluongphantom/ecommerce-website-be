package com.ptit.e_commerce_website_be.do_an_nhom.services;

import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Orders;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.OrdersRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrdersService {

    private final OrdersRepository ordersRepository;

    public OrdersService(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    public List<String> getAddressDetailsByShopId(Long shopId) {
        return ordersRepository.findAddressDetailByShopId(shopId);
    }

    public List<Orders> getCompletedOrders(int month, int year) {
        // Tính khoảng thời gian bắt đầu và kết thúc trong tháng và năm được nhập
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        // Gọi repository để lấy danh sách khách hàng có trạng thái COMPLETED
        return ordersRepository.findByStatusAndCreatedAtBetween(Orders.OrderStatus.COMPLETED, startDate, endDate);
    }

//    public OrdersService(OrdersRepository ordersRepository) {
//        this.ordersRepository = ordersRepository;
//    }

    public List<Map<String, Object>> getRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = ordersRepository.findRevenueByDateRange(startDate, endDate);
        List<Map<String, Object>> revenues = new ArrayList<>();
        for (Object[] result : results) {
            Map<String, Object> revenue = new HashMap<>();
            revenue.put("date", result[0]); // Ngày
            revenue.put("revenue", result[1]); // Doanh thu
            revenues.add(revenue);
        }
        return revenues;
    }

    public List<Map<String, Object>> getRevenueByDateRangeAndShopId(
            LocalDateTime startDate, LocalDateTime endDate, Long shopId) {
        List<Object[]> results = ordersRepository.findRevenueByDateRangeAndShopId(startDate, endDate, shopId);
        List<Map<String, Object>> revenues = new ArrayList<>();
        for (Object[] result : results) {
            Map<String, Object> revenue = new HashMap<>();
            revenue.put("date", result[0]); // Ngày
            revenue.put("revenue", result[1]); // Doanh thu
            revenues.add(revenue);
        }
        return revenues;
    }

    public Map<String, Long> getOrderCountsByShopId(Long shopId) {
        Long pendingCount = ordersRepository.countByShopIdAndStatus(shopId, Orders.OrderStatus.PENDING);
        Long cancelledCount = ordersRepository.countByShopIdAndStatus(shopId, Orders.OrderStatus.CANCELLED);
        Long completedCount = ordersRepository.countByShopIdAndStatus(shopId, Orders.OrderStatus.COMPLETED);

        Map<String, Long> response = new HashMap<>();
        response.put("pendingOrders", pendingCount);
        response.put("cancelledOrders", cancelledCount);
        response.put("completedOrders", completedCount);

        return response;
    }
}
