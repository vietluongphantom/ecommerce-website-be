package com.ptit.e_commerce_website_be.do_an_nhom.controllers;

import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.DataNotFoundException;
import com.ptit.e_commerce_website_be.do_an_nhom.mapper.OrderItemMapper;
import com.ptit.e_commerce_website_be.do_an_nhom.mapper.OrderMapper;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.OrdersDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.OrderStatusHistory;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Orders;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Shop;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.OrderItemDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.ProductDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.*;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.CommonResult;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.OrderStatusHistoryRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.ProductItemRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.ProductRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.OrderItemRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.OrdersRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.ShopRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.services.OrdersService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.orders.IOrdersService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.productitem.ProductItemService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
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
    public CommonResult<Page<OrdersDTO>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long id
    ) {
        // Lấy thông tin user từ SecurityContext
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Tạo PageRequest để phân trang và sắp xếp
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").descending());

        // Lấy danh sách orders từ service
        Page<Orders> ordersPage = iOrdersService.findByShopIdAndId(user.getId(), pageRequest, id);

        // Chuyển đổi Page<Orders> thành Page<OrdersDTO>
        Page<OrdersDTO> ordersDTOPage = ordersPage.map(orderMapper::toDto);

        // Trả về kết quả
        return CommonResult.success(ordersDTOPage, "Get orders successfully");
    }



    @GetMapping("/{id}")
    public CommonResult<OrdersDTO> getOrderById(@PathVariable Long id) {
        return iOrdersService.findById(id)
                .map(order -> {
                    // Lấy danh sách OrderItem từ OrderItemRepository
                    List<OrderItem> orderItems = iOrdersService.getOrderItems(order.getId());

                    // Chuyển danh sách OrderItem sang OrderItemDTO
                    List<OrderItemDTO> orderItemDTOs = OrderItemMapper.toDtoList(orderItems);

                    // Chuyển đổi Orders sang OrdersDTO
                    OrdersDTO orderDto = orderMapper.toDto(order);
                    orderDto.setOrderItems(orderItemDTOs); // Gắn danh sách OrderItemDTO vào OrdersDTO

                    return CommonResult.success(orderDto, "Get order successfully");
                })
                .orElse(CommonResult.error(404, "Order not found"));
    }



    @GetMapping("/user")
    public CommonResult<Page<OrdersDTO>> getUserOrders(
            @RequestParam(required = false) Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").descending());

        Page<Orders> ordersPage;
        if (id != null) {
            // Nếu có id, tìm order cụ thể của user
            ordersPage = iOrdersService.findByUserIdAndId(user.getId(), id, pageRequest);
        } else {
            // Nếu không có id, lấy tất cả orders của user với phân trang
            ordersPage = iOrdersService.findByUserIdWithPagination(user.getId(), pageRequest);
        }

        Page<OrdersDTO> ordersDTOPage = ordersPage.map(orderMapper::toDto);
        return CommonResult.success(ordersDTOPage, "Get user orders successfully");
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

    @GetMapping("/admin")
    public CommonResult<Page<OrdersDTO>> getAllOrdersForAdmin(
            @RequestParam(required = false) Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Orders> ordersPage;


        ordersPage = iOrdersService.findAllForAdmin(id, pageRequest);


        Page<OrdersDTO> ordersDTOPage = ordersPage.map(orderMapper::toDto);
        return CommonResult.success(ordersDTOPage, "Get all orders for admin successfully");
    }


    private final ShopRepository shopRepository;


    @GetMapping("/address-detail")
    public ResponseEntity<Map<String, Integer>> getAddressDetails() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Shop shopOpt = shopRepository.findByUserId(user.getId());
        if (shopOpt != null) {
            Long shopId = shopOpt.getId();
            List<String> addressDetails = ordersService.getAddressDetailsByShopId(shopId);

            List<String> provinces = Arrays.asList(
                    "Ha Noi", "Ho Chi Minh", "Hai Phong", "Can Tho", "Da Nang", "Binh Duong",
                    "Dong Nai", "Quang Ninh", "Kien Giang", "Khanh Hoa", "Nghe An", "Hai Duong",
                    "Ha Tinh", "Thanh Hoa", "Soc Trang", "Ben Tre", "Binh Dinh", "Dak Lak",
                    "Dak Nong", "Lai Chau", "Lam Dong", "Lang Son", "Lao Cai", "Quang Nam",
                    "Quang Tri", "Quang Binh", "Ninh Binh", "Ninh Thuan", "Gia Lai", "Phu Tho",
                    "Tien Giang", "Tra Vinh", "Bac Ninh", "Bac Giang", "Yen Bai", "Hoa Binh",
                    "Ha Giang", "Cao Bang", "Bac Kan", "Tuyen Quang", "Thai Nguyen", "Phu Yen",
                    "Binh Phuoc", "Ba Ria Vung Tau", "Vinh Phuc", "Thai Binh", "Nam Dinh", "Hung Yen",
                    "Long An", "An Giang", "Dong Thap", "Vinh Long", "Hau Giang", "Bac Lieu",
                    "Ca Mau", "Son La", "Dien Bien", "Kon Tum", "Tay Ninh", "Lai Chau", "Hoa Binh",
                    "Quang Ngai"
            );

            // Tạo Map để lưu số lần xuất hiện
            Map<String, Integer> provinceCount = new HashMap<>();
            provinceCount.put("Khác", 0);

            // Đếm số lần xuất hiện
            for (String address : addressDetails) {
                String[] parts = address.split(",");
                if (parts.length > 0) {
                    String province = parts[0].trim();

                    // Chuyển tên tỉnh và địa chỉ về không dấu
                    String normalizedProvince = removeVietnameseAccents(province).toLowerCase();
                    boolean matched = false;

                    for (String standardProvince : provinces) {
                        if (removeVietnameseAccents(standardProvince).toLowerCase().equals(normalizedProvince)) {
                            provinceCount.put(standardProvince, provinceCount.getOrDefault(standardProvince, 0) + 1);
                            matched = true;
                            break;
                        }
                    }

                    if (!matched) {
                        provinceCount.put("Khác", provinceCount.get("Khác") + 1);
                    }
                }
            }

            // Lọc bỏ các tỉnh có số lượng = 0
            Map<String, Integer> filteredProvinceCount = provinceCount.entrySet().stream()
                    .filter(entry -> entry.getValue() > 0)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            return ResponseEntity.ok(filteredProvinceCount);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyMap());
        }
    }

    public static String removeVietnameseAccents(String str) {
        str = Normalizer.normalize(str, Normalizer.Form.NFD);
        str = str.replaceAll("\\p{M}", "");
        return str;
    }





    @GetMapping("/completed")
    public List<Orders> getCompletedOrders(
            @RequestParam int month,
            @RequestParam int year) {
        return ordersService.getCompletedOrders(month, year);
    }



    @GetMapping("/revenue")
    public ResponseEntity<List<Map<String, Object>>> getRevenueByDateRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Map<String, Object>> revenues = ordersService.getRevenueByDateRange(startDate, endDate);
        return ResponseEntity.ok(revenues);
    }



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


    @GetMapping("/status/{status}")
    public CommonResult<Page<OrdersDTO>> getOrdersByStatus(
            @PathVariable Orders.OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "",required = false) Long id
    ) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<Orders> ordersPage = iOrdersService.findByStatusWithPagination(status, user.getId(), pageRequest, id );
        Page<OrdersDTO> ordersDTOPage = ordersPage.map(orderMapper::toDto);
        return CommonResult.success(ordersDTOPage, "Get orders by status successfully");
    }

    @GetMapping("/seller/status/{status}")
    public CommonResult<Page<OrdersDTO>> getOrdersByStatusBySeller(
            @PathVariable Orders.OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "",required = false) Long id
    ) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Orders> ordersPage = iOrdersService.findByStatusWithPaginationBySeller(status, user.getId(), pageRequest, id );
        Page<OrdersDTO> ordersDTOPage = ordersPage.map(orderMapper::toDto);
        return CommonResult.success(ordersDTOPage, "Seller get orders by status successfully");
    }



    @GetMapping("/admin/status/{status}")
    public CommonResult<Page<OrdersDTO>> getOrdersByStatusByAdmin(
            @PathVariable Orders.OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "",required = false) Long id
    ) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Orders> ordersPage = iOrdersService.findByStatusWithPaginationByAdmin(status,  pageRequest, id );
        Page<OrdersDTO> ordersDTOPage = ordersPage.map(orderMapper::toDto);
        return CommonResult.success(ordersDTOPage, "Admin get orders by status successfully");
    }

}
