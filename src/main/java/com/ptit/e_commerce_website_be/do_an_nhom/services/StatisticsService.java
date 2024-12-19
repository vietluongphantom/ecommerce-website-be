package com.ptit.e_commerce_website_be.do_an_nhom.services;

import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Orders;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.OrderStatusHistoryRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.OrdersRepository;
@Service
public class StatisticsService {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrderStatusHistoryRepository orderStatusHistoryRepository;
/////
    public BigDecimal getTotalRevenue() {
        return ordersRepository.getTotalRevenue();
    }



    public List<Map<String, Object>> getDailyRevenue(int month, int year) {
        List<Object[]> results = ordersRepository.getDailyRevenue(month, year);
        return results.stream().map(record -> {
            Map<String, Object> map = new HashMap<>();
            map.put("date", record[0]); // record[0] là ngày
            map.put("revenue", record[1]); // record[1] là doanh thu
            return map;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getDailyRevenueByShop(int month, int year, Long shopId) {
        List<Object[]> results = ordersRepository.getDailyRevenueByShop(month, year, shopId);
        return results.stream().map(record -> {
            Map<String, Object> map = new HashMap<>();
            map.put("date", record[0]);
            map.put("revenue", record[1]);
            return map;
        }).collect(Collectors.toList());
    }


//    public List<Map<String, Object>> getDailyRevenue() {
//        List<Object[]> results = ordersRepository.getDailyRevenue();
//        return results.stream().map(record -> {
//            Map<String, Object> map = new HashMap<>();
//            map.put("date", record[0]);
//            map.put("revenue", record[1]);
//            return map;
//        }).collect(Collectors.toList());
//    }

//    public BigDecimal getTotalRevenue(LocalDate startDate, LocalDate endDate) {
//        return ordersRepository.getTotalRevenue(startDate, endDate);
//    }




//    public List<Map<String, Object>> getMonthlyRevenue() {
//        List<Object[]> results = ordersRepository.getMonthlyRevenue();
//        return results.stream().map(record -> {
//            Map<String, Object> map = new HashMap<>();
//            map.put("month", record[0]);
//            map.put("revenue", record[1]);
//            return map;
//        }).collect(Collectors.toList());
//    }

    public List<Map<String, Object>> getMonthlyRevenue(int year) {
        List<Object[]> results = ordersRepository.getMonthlyRevenue(year);
        return results.stream().map(record -> {
            Map<String, Object> map = new HashMap<>();
            map.put("month", record[0]); // Tháng
            map.put("revenue", record[1]); // Doanh thu
            return map;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getMonthlyRevenueByShop(int year, Long shopId) {
        List<Object[]> results = ordersRepository.getMonthlyRevenueByShop(year, shopId);
        return results.stream().map(record -> {
            Map<String, Object> map = new HashMap<>();
            map.put("month", record[0]);
            map.put("revenue", record[1]);
            return map;
        }).collect(Collectors.toList());
    }


    // Giữ các hàm cũ không thay đổi
    public Long getTotalCancelledOrders() {
        return orderStatusHistoryRepository.countCancelledOrders();
    }

    public Long getTotalCompletedOrders() {
        return orderStatusHistoryRepository.countCompletedOrders();
    }

    public Long getTotalPendingOrders() {
        return orderStatusHistoryRepository.countPendingOrders();
    }

}

