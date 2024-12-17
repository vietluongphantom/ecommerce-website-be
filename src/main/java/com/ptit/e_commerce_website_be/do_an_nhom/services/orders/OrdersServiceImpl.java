package com.ptit.e_commerce_website_be.do_an_nhom.services.orders;

import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.DataNotFoundException;
import com.ptit.e_commerce_website_be.do_an_nhom.mapper.OrderItemMapper;
import com.ptit.e_commerce_website_be.do_an_nhom.mapper.OrderMapper;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.OrderItemDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.OrdersDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.OrderItem;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.OrderStatusHistory;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Orders;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.*;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.*;
import com.ptit.e_commerce_website_be.do_an_nhom.services.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrdersServiceImpl implements IOrdersService {

    private final OrderMapper orderMapper;
    private final OrdersRepository ordersRepository;
    private final UserRepository userRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShopRepository shopRepository;
    private final AddressRepository addressRepository;

    @Override
    public OrdersDTO addOrder(OrdersDTO orderDTO, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new DataNotFoundException("Cannot find user by this id"));
        Address address = addressRepository.findByUserId(userId)
                .orElseThrow(()-> new DataNotFoundException("Cannot find address by userId"));
        String addressReceiver = address.getCommune() + ", " + address.getDistrict() + ", " + address.getProvince() + "," + address.getCountry();
        Orders order = orderMapper.toEntity(orderDTO);
        order.setAddress(addressReceiver);
        order.setAddressDetail(address.getAddressDetail());
        order.setBuyer(user.getFullName());
        order.setReceiverPhone(user.getPhone());
        order.setShopId(orderDTO.getShopId());
        ordersRepository.save(order);
        return orderDTO;
    }

    @Override
    public List<Orders> findAll(Long userId){
        User user = userRepository.findById(userId).orElseThrow(()-> new DataNotFoundException("Cannot find user by this id"));
        Address address = addressRepository.findByUserId(userId)
                .orElseThrow(()-> new DataNotFoundException("Cannot find address by userId"));
        String addressReceiver = address.getCommune() + ", " + address.getDistrict() + ", " + address.getProvince() + "," + address.getCountry();
        Shop shop = shopRepository.findByUserId(userId);
        List<Orders> ordersList = ordersRepository.findAll(shop.getId());

        return ordersList;
    }

    @Override
    public Optional<Orders> findById(Long id) {
        return ordersRepository.findById(id);
    }

    @Override
    public Orders save(Orders orders) {
        return ordersRepository.save(orders);
    }

    @Override
    public void deleteById(Long id) {
        ordersRepository.deleteById(id);
    }

    @Override
    public List<Orders> findByUserId(Long userId) {
        return ordersRepository.findByUserId(userId); // Giả sử repository đã có phương thức này
    }

    @Override
    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }



    @Override
    public List<Orders> getAllOrderBySeller(Long userId){
        return List.of();
    }

    @Override
    public void updateOrderStatus(Long orderId, Orders.OrderStatus newStatus) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new DataNotFoundException("Order not found"));
        // Cập nhật trạng thái đơn hàng
        order.setStatus(newStatus);
        order.setModifiedAt(LocalDateTime.now());
        ordersRepository.save(order);
        // Lưu vào lịch sử thay đổi trạng thái
        OrderStatusHistory history = OrderStatusHistory.builder()
                .orderId(orderId)
                .userId(order.getUserId())
                .status(newStatus)
                .build();
        orderStatusHistoryRepository.save(history);
    }

    @Override
    public List<OrderStatusHistory> getOrderHistory(Long orderId) {
        return orderStatusHistoryRepository.findByOrderId(orderId);
    }


    public boolean checkUserPurchasedProduct(Long userId, Long productId) {
        return orderItemRepository.hasUserPurchasedProduct(userId, productId);
    }
//    @Override
//    public List<OrderItemDTO> findOrderItemsByUserId(Long userId) {
//        // Lấy danh sách các OrderItem dựa trên userId
//        List<OrderItem> orderItems = orderItemRepository.findByOrderId(userId); // Giả định rằng bạn đã định nghĩa phương thức này trong OrderItemRepository
//
//        // Chuyển đổi danh sách OrderItem sang danh sách OrderItemDTO
//        return orderItems.stream()
//                .map(orderMapper::toOrderItemDTO) // Sử dụng orderMapper để chuyển đổi
//                .collect(Collectors.toList());
//    }
    @Override
    public List<Orders> findAllForAdmin() {
        return ordersRepository.findAllByAdmin();
    }

}