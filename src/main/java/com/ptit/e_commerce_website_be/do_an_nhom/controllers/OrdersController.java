package com.ptit.e_commerce_website_be.do_an_nhom.controllers;

import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.DataNotFoundException;
import com.ptit.e_commerce_website_be.do_an_nhom.mapper.OrderMapper;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.OrdersDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.OrderStatusHistory;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Orders;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Shop;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.CommonResult;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.OrderStatusHistoryRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.OrdersRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.ShopRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.services.OrdersService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.orders.IOrdersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final OrderMapper orderMapper;
    private final IOrdersService iOrdersService;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final OrdersService ordersService;
    private final OrdersRepository ordersRepository;

    @GetMapping
    public CommonResult<List<OrdersDTO>> getAllOrders(){
        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<OrdersDTO> orders = iOrdersService.findAll(user.getId()).stream().map(orderMapper::toDto).collect(Collectors.toList());
        return CommonResult.success(orders, "Get all orders successfully");

    }

    @GetMapping("/{id}")
    public CommonResult<OrdersDTO> getOrderById(@PathVariable Long id) {
        return iOrdersService.findById(id)
                .map(order -> CommonResult.success(orderMapper.toDto(order), "Get order successfully"))
                .orElse(CommonResult.error(404, "Order not found"));
    }

    @GetMapping("/user")
    public CommonResult<List<OrdersDTO>> getUserOrders() {
        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Orders> ordersList = iOrdersService.findByUserId(user.getId());
        List<OrdersDTO> ordersDTOList = ordersList.stream().map(orderMapper::toDto).collect(Collectors.toList());
        return CommonResult.success(ordersDTOList, "Get user orders successfully");
    }


    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public CommonResult<OrdersDTO> createOrder(@Valid @RequestBody OrdersDTO ordersDTO){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success(iOrdersService.addOrder(ordersDTO, user.getId()));
//        Orders order = orderMapper.toEntity(ordersDTO);
//        Orders savedOrder = iOrdersService.save(order);
//        return CommonResult.success(orderMapper.toDto(savedOrder), "Create order successfully");
    }


    @PutMapping("/{id}")
    public CommonResult<OrdersDTO> updateOrder(@PathVariable Long id, @RequestBody OrdersDTO orderDetails) {
        return iOrdersService.findById(id)
                .map(order -> {
                    order.setNote(orderDetails.getNote());
                    order.setStatus(Orders.OrderStatus.valueOf(orderDetails.getStatus().toString()));
                    order.setTotalPrice(orderDetails.getTotalPrice());
                    order.setMethod(orderDetails.isMethod());
                    Orders updatedOrder = iOrdersService.save(order);
                    return CommonResult.success(orderMapper.toDto(updatedOrder), "Update order successfully");
                }).orElse(CommonResult.error(404, "Order not found"));
    }



    @PatchMapping("/{id}")
    public CommonResult<OrdersDTO> patchOrder(@PathVariable Long id, @RequestBody OrdersDTO orderDetails) {
        return iOrdersService.findById(id)
                .map(order -> {
                    if (orderDetails.getNote() != null) order.setNote(orderDetails.getNote());
                    if (orderDetails.getStatus() != null)
                        order.setStatus(Orders.OrderStatus.valueOf(orderDetails.getStatus().toString()));
                    if (orderDetails.getTotalPrice() != null) order.setTotalPrice(orderDetails.getTotalPrice());
                    if (orderDetails.isMethod()) order.setMethod(orderDetails.isMethod());
                    Orders updatedOrder = iOrdersService.save(order);
                    return CommonResult.success(orderMapper.toDto(updatedOrder), "Patch order successfully");
                }).orElse(CommonResult.error(404, "Order not found"));
    }


    @DeleteMapping("/{id}")
    public CommonResult<String> deleteOrder(@PathVariable Long id) {
        return iOrdersService.findById(id)
                .map(order -> {
                    iOrdersService.deleteById(id);
                    return CommonResult.success("Order with ID " + id + " has been deleted.");
                })
                .orElse(CommonResult.error(404, "Order not found"));
    }


    // API để cập nhật trạng thái đơn hàng
    @PutMapping("/{orderId}/status")
    public CommonResult<Object> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam Orders.OrderStatus status
    ) {
        try {
            iOrdersService.updateOrderStatus(orderId, status);
            return CommonResult.success("Order status updated successfully");
        } catch (Exception e) {
            return CommonResult.forbidden("Failed to update order status: " + e.getMessage());
        }
    }

    @GetMapping("/{orderId}/history")
    public CommonResult<List<OrderStatusHistory>> getOrderStatusHistory(@PathVariable Long orderId) {
        List<OrderStatusHistory> history = iOrdersService.getOrderHistory(orderId);
        if (history.isEmpty()) {
            return CommonResult.error(404, "No history found for order with ID " + orderId);
        }
        return CommonResult.success(history);
    }

    @GetMapping("/check-purchase/{productId}")
    public ResponseEntity<CommonResult<Boolean>> checkUserPurchasedProduct(
            @PathVariable("productId") Long productId) {

        // Lấy userId từ SecurityContextHolder
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Kiểm tra xem người dùng đã mua sản phẩm chưa
        boolean hasPurchased = iOrdersService.checkUserPurchasedProduct(user.getId(), productId);
        if (hasPurchased) {
            return ResponseEntity.ok(CommonResult.success(hasPurchased, "User has purchased the product"));
        } else {
            return ResponseEntity.ok(CommonResult.failed("User has not purchased the product"));
        }
    }

//    private final OrdersService ordersService;

//    public OrdersController(OrdersService ordersService) {
//        this.ordersService = ordersService;
//    }

    private final ShopRepository shopRepository;
//    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//    Optional<Shop> shopOpt = shopRepository.findShopByUserId(user.getId());
//    Long shopId = shopOpt.map(Shop::getId).orElse(null);

    //17/12
//    @GetMapping("/address-detail")
//    public ResponseEntity<List<String>> getAddressDetailsByShopId(@RequestParam("shopId") Long shopId) {
//        List<String> addressDetails = ordersService.getAddressDetailsByShopId(shopId);
//        return ResponseEntity.ok(addressDetails);
//    }


    @GetMapping("/address-detail")
    public ResponseEntity<List<String>> getAddressDetails() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Lấy thông tin shopId từ userId thông qua repository
        Shop shopOpt = shopRepository.findByUserId(user.getId());
        if (shopOpt != null) {
            Long shopId = shopOpt.getId();

            // Gọi service logic với shopId
            List<String> addressDetails = ordersService.getAddressDetailsByShopId(shopId);
            return ResponseEntity.ok(addressDetails);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
    }

//    @GetMapping("/address-detail/count")
//    public ResponseEntity<Map<String, Integer>> getCountOfProvinces(@RequestParam("shopId") Long shopId) {
//        List<String> addressDetails = ordersService.getAddressDetailsByShopId(shopId);
//        Map<String, Integer> provinceCount = countProvinces(addressDetails);
//        return ResponseEntity.ok(provinceCount);
//    }

    /**
     * Hàm nhận diện và tính toán số lượng tỉnh thành trong danh sách địa chỉ.
     */
//    private Map<String, Integer> countProvinces(List<String> addressDetails) {
//        // Danh sách các tỉnh thành của Việt Nam
//        List<String> provinces = Arrays.asList(
//                "Ha Noi", "Ho Chi Minh", "Hai Phong", "Can Tho", "Da Nang", "Binh Duong",
//                "Dong Nai", "Quang Ninh", "Kien Giang", "Khanh Hoa", "Nghe An", "Hai Duong",
//                "Ha Tinh", "Thanh Hoa", "Soc Trang", "Ben Tre", "Binh Dinh", "Dak Lak",
//                "Dak Nong", "Lai Chau", "Lam Dong", "Lang Son", "Lao Cai", "Quang Nam",
//                "Quang Tri", "Quang Binh", "Ninh Binh", "Ninh Thuan", "Gia Lai", "Phu Tho",
//                "Tien Giang", "Tra Vinh", "Bac Ninh", "Bac Giang", "Yen Bai", "Hoa Binh",
//                "Ha Giang", "Cao Bang", "Bac Kan", "Tuyen Quang", "Thai Nguyen", "Phu Yen",
//                "Binh Phuoc", "Ba Ria Vung Tau", "Vinh Phuc", "Thai Binh", "Nam Dinh", "Hung Yen",
//                "Long An", "An Giang", "Dong Thap", "Vinh Long", "Hau Giang", "Bac Lieu",
//                "Ca Mau", "Son La", "Dien Bien", "Kon Tum", "Tay Ninh", "Lai Chau", "Hoa Binh",
//                "Quang Ngai"
//        );
//
//
//        // Tạo bản đồ đếm
//        Map<String, Integer> provinceCountMap = new HashMap<>();
//
//        for (String address : addressDetails) {
//            boolean found = false;
//            for (String province : provinces) {
//                if (address.contains(province)) { // Kiểm tra địa chỉ chứa tỉnh thành
//                    provinceCountMap.put(province, provinceCountMap.getOrDefault(province, 0) + 1);
//                    found = true;
//                    break;
//                }
//            }
//
//            if (!found) { // Nếu không tìm thấy trong danh sách
//                provinceCountMap.put("Khác", provinceCountMap.getOrDefault("Khác", 0) + 1);
//            }
//        }
//
//        return provinceCountMap;
//    }



    @GetMapping("/completed")
    public List<Orders> getCompletedOrders(
            @RequestParam int month,
            @RequestParam int year) {
        return ordersService.getCompletedOrders(month, year);
    }

//    public List<Long> getCompletedUserIds(int month, int year) {
//        YearMonth yearMonth = YearMonth.of(year, month);
//        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
//        LocalDateTime endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
//
//        return ordersRepository
//                .findByStatusAndCreatedAtBetween(Orders.OrderStatus.COMPLETED, startDate, endDate)
//                .stream()
//                .map(Orders::getUserId) // Chỉ lấy userId
//                .distinct() //
//                .collect(Collectors.toList());
//    }

//    @GetMapping("/completed")
//    public List<Long> getCompletedUserIds(
//            @RequestParam int month,
//            @RequestParam int year) {
//        return ordersService.getCompletedUserIds(month, year);
//    }

    @GetMapping("/revenue")
    public ResponseEntity<List<Map<String, Object>>> getRevenueByDateRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Map<String, Object>> revenues = ordersService.getRevenueByDateRange(startDate, endDate);
        return ResponseEntity.ok(revenues);
    }

//    @GetMapping("/revenueByShop")
//    public ResponseEntity<List<Map<String, Object>>> getRevenueByDateRangeAndShopId(
//            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
//            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
//            @RequestParam("shopId") Long shopId) {
//        List<Map<String, Object>> revenues = ordersService.getRevenueByDateRangeAndShopId(startDate, endDate, shopId);
//        return ResponseEntity.ok(revenues);
//    }

    @GetMapping("/revenueByShop")
    public ResponseEntity<List<Map<String, Object>>> getRevenueByDateRangeAndShopId(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        // Lấy thông tin user hiện tại từ SecurityContextHolder
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Dùng repository tìm thông tin shopId liên quan tới userId
        Shop shopOpt = shopRepository.findByUserId(user.getId());
        if (shopOpt != null) {
            Long shopId = shopOpt.getId();

            // Gọi service logic để lấy thông tin doanh thu
            List<Map<String, Object>> revenues = ordersService.getRevenueByDateRangeAndShopId(startDate, endDate, shopId);
            return ResponseEntity.ok(revenues);
        } else {
            // Nếu không tìm thấy thông tin shopId thì trả về 404 NOT FOUND
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
    }


}
