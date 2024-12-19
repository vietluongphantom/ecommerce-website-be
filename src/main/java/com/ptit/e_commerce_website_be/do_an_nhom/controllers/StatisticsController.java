package com.ptit.e_commerce_website_be.do_an_nhom.controllers;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.RevenueAndProfitResponse;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Shop;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.ShopRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.services.OrdersService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.ProfitCalculationService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;
    private ShopRepository shopRepository;

    public StatisticsController(ProfitCalculationService profitCalculationService) {
        this.profitCalculationService = profitCalculationService;
    }

    /////
    @GetMapping("/revenue/total")
    public ResponseEntity<BigDecimal> getTotalRevenue() {
        return ResponseEntity.ok(statisticsService.getTotalRevenue());
    }



//    @GetMapping("/revenue/daily")
//    public ResponseEntity<List<Map<String, Object>>> getDailyRevenue() {
//        return ResponseEntity.ok(statisticsService.getDailyRevenue());
//    }




//    @GetMapping("/revenue/monthly")
//    public ResponseEntity<List<Map<String, Object>>> getMonthlyRevenue() {
//        return ResponseEntity.ok(statisticsService.getMonthlyRevenue());
//    }

//    @GetMapping("/revenue/monthly")
//    public ResponseEntity<List<Map<String, Object>>> getMonthlyRevenue(@RequestParam int year) {
//        return ResponseEntity.ok(statisticsService.getMonthlyRevenue(year));
//    }

    @GetMapping("/revenue/monthly")
    public ResponseEntity<List<Map<String, Object>>> getMonthlyRevenue(
            @RequestParam int year,
            @RequestParam(required = false) Long shopId) {

        if (shopId != null) {
            return ResponseEntity.ok(statisticsService.getMonthlyRevenueByShop(year, shopId));
        }

        return ResponseEntity.ok(statisticsService.getMonthlyRevenue(year));
    }

    /////
    @GetMapping("/orders")
    public ResponseEntity<Map<String, Long>> getOrderStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("completedOrders", statisticsService.getTotalCompletedOrders());
        stats.put("cancelledOrders", statisticsService.getTotalCancelledOrders());
        stats.put("pendingOrders", statisticsService.getTotalPendingOrders());
        return ResponseEntity.ok(stats);
    }

    @Autowired
    private OrdersService ordersService;

//    @GetMapping("/status/{shopId}")
//    public ResponseEntity<Map<String, Long>> getOrderStatusCounts(@PathVariable Long shopId) {
//        Map<String, Long> counts = ordersService.getOrderCountsByShopId(shopId);
//        return ResponseEntity.ok(counts);
//    }

    @GetMapping("/ordersByShop")
    public ResponseEntity<Map<String, Long>> getOrderStatusCounts(@RequestParam("shopID") Long shopId) {
        Map<String, Long> counts = ordersService.getOrderCountsByShopId(shopId);
        return ResponseEntity.ok(counts);
    }


//    @GetMapping("/ordersByShop")
//    public ResponseEntity<Map<String, Long>> getOrderStatusCounts() {
//        // Lấy thông tin user từ SecurityContextHolder
//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//        // Lấy thông tin shopId từ userId thông qua repository
//        Shop shopOpt = shopRepository.findByUserId(user.getId());
//        if (shopOpt != null) {
//            Long shopId = shopOpt.getId();
//
//            // Gọi service logic với shopId
//            Map<String, Long> counts = ordersService.getOrderCountsByShopId(shopId);
//            return ResponseEntity.ok(counts);
//        } else {
//            // Trả về 404 nếu không tìm thấy shop
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyMap());
//        }
//    }




    @GetMapping("/revenue/daily")
    public ResponseEntity<List<Map<String, Object>>> getDailyRevenue(
            @RequestParam("month") int month,
            @RequestParam("year") int year) {
        return ResponseEntity.ok(statisticsService.getDailyRevenue(month, year));
    }

    @GetMapping("/revenue/daily/shop")
    public ResponseEntity<List<Map<String, Object>>> getDailyRevenueByShop(
            @RequestParam("month") int month,
            @RequestParam("year") int year,
            @RequestParam("shopId") Long shopId) {
        return ResponseEntity.ok(statisticsService.getDailyRevenueByShop(month, year, shopId));
    }

    private final ProfitCalculationService profitCalculationService;

    @GetMapping("/profit")
    public ResponseEntity<RevenueAndProfitResponse> calculateRevenueAndProfit(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        RevenueAndProfitResponse response = profitCalculationService.calculateRevenueAndProfit(startDate, endDate);
        return ResponseEntity.ok(response);
    }

//    @GetMapping("/current")
//    public CommonResult<Long> getCurrentUserId() {
//        // Lấy thông tin người dùng hiện tại từ SecurityContextHolder
//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//        // Trả về userId thông qua CommonResult
//        return CommonResult.success(user.getId(), "Get current userID successfully");
//    }

    @GetMapping("/profitByShop")
    public ResponseEntity<RevenueAndProfitResponse> calculateRevenueAndProfitByShop(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam("shopId") Long shopId) {

        RevenueAndProfitResponse response = profitCalculationService.calculateRevenueAndProfitByShop(startDate, endDate, shopId);
        return ResponseEntity.ok(response);
    }


//    @GetMapping("/profitByShop")
//    public ResponseEntity<RevenueAndProfitResponse> calculateRevenueAndProfitByShop(
//            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
//            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
//
//        // Lấy thông tin người dùng hiện tại từ SecurityContextHolder
//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//        // Lấy thông tin shopId từ repository
//        Shop shopOpt = shopRepository.findByUserId(user.getId());
//        if (shopOpt != null) {
//            Long shopId = shopOpt.getId();
//
//            // Gọi service để tính toán lợi nhuận và doanh thu
//            RevenueAndProfitResponse response = profitCalculationService.calculateRevenueAndProfitByShop(startDate, endDate, shopId);
//            return ResponseEntity.ok(response);
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//    }

}
