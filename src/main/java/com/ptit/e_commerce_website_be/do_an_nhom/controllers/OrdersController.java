package com.ptit.e_commerce_website_be.do_an_nhom.controllers;

import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.DataNotFoundException;
import com.ptit.e_commerce_website_be.do_an_nhom.mapper.OrderItemMapper;
import com.ptit.e_commerce_website_be.do_an_nhom.mapper.OrderMapper;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.OrdersDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.OrderItemDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.OrderItem;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.OrderStatusHistory;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Orders;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.CommonResult;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.OrderStatusHistoryRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.OrderItemRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.services.orders.IOrdersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final OrderMapper orderMapper;
    private final IOrdersService iOrdersService;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;

    @GetMapping
    public CommonResult<List<OrdersDTO>> getAllOrders(){
        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<OrdersDTO> orders = iOrdersService.findAll(user.getId()).stream().map(orderMapper::toDto).collect(Collectors.toList());
        return CommonResult.success(orders, "Get all orders successfully");

    }

//    @GetMapping("/{id}")
//    public CommonResult<OrdersDTO> getOrderById(@PathVariable Long id) {
//        return iOrdersService.findById(id)
//                .map(order -> CommonResult.success(orderMapper.toDto(order), "Get order successfully"))
//                .orElse(CommonResult.error(404, "Order not found"));
//    }
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
    public CommonResult<List<OrdersDTO>> getUserOrders() {
        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Orders> ordersList = iOrdersService.findByUserId(user.getId());
        List<OrdersDTO> ordersDTOList = ordersList.stream().map(orderMapper::toDto).collect(Collectors.toList());
        return CommonResult.success(ordersDTOList, "Get user orders successfully");
    }

//    @GetMapping("/user/order-items")
//    public CommonResult<List<OrderItemDTO>> getUserOrderItems() {
//        // Lấy thông tin người dùng từ SecurityContextHolder
//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//        // Lấy danh sách các OrderItems từ OrdersService bằng userId
//        List<OrderItemDTO> orderItems = iOrdersService.findOrderItemsByOrder(user.getId());
//
//        if (orderItems.isEmpty()) {
//            return CommonResult.error(404, "No order items found for the user.");
//        }
//        return CommonResult.success(orderItems, "Get order items successfully.");
//    }

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
}
