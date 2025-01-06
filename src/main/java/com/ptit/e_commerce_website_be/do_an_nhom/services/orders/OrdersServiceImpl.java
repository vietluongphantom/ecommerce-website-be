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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final ProductItemRepository productItemRepository;


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
    public Page<Orders> findByShopIdAndId(Long userId, PageRequest pageRequest, Long id ){
        User user = userRepository.findById(userId).orElseThrow(()-> new DataNotFoundException("Cannot find user by this id"));
        Address address = addressRepository.findByUserId(userId)
                .orElseThrow(()-> new DataNotFoundException("Cannot find address by userId"));
        String addressReceiver = address.getCommune() + ", " + address.getDistrict() + ", " + address.getProvince() + "," + address.getCountry();
        Shop shop = shopRepository.findByUserId(userId);
        Page<Orders> ordersList = ordersRepository.findByShopIdAndId(shop.getId(), pageRequest, id);

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
                .message(createMessage(newStatus))
                .isRead(Boolean.TRUE)
                .build();
        orderStatusHistoryRepository.save(history);

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        for(int j = 0 ; j < orderItems.size(); j ++){
            ProductItem productItem =  productItemRepository.findById(orderItems.get(j).getProductItemId()).get();
            productItem.setQuantity(productItem.getQuantity()+ orderItems.get(j).getQuantity());
            productItemRepository.save(productItem);
        }
    }

    @Override
    public List<OrderStatusHistory> getOrderHistory(Long orderId) {
        return orderStatusHistoryRepository.findByOrderId(orderId);
    }


    public boolean checkUserPurchasedProduct(Long userId, Long productId) {
        return orderItemRepository.hasUserPurchasedProduct(userId, productId);
    }

    @Override
    public Page<Orders> findAllForAdmin(Long id, PageRequest pageRequest ) {
        return ordersRepository.findAllByAdmin(id, pageRequest);
    }



    @Override
    public Page<Orders> findByStatusWithPagination(Orders.OrderStatus status, Long userId, PageRequest pageRequest, Long id) {
//        return ordersRepository.findByStatusAndUserIdAndId(status, userId, pageRequest, id);
        if (id != null) {
            return ordersRepository.findByStatusAndUserIdAndId(status, userId, pageRequest,id);
        } else {
            return ordersRepository.findByStatusAndUserId(status, userId, pageRequest);
        }
    }

    //
//    @Override
//    public List<Orders> findByUserIdAndId(Long userId, Long id) {
//        return ordersRepository.findByUserIdAndId(userId, id);
//    }
    @Override
    public Page<Orders> findByUserIdWithPagination(Long userId, PageRequest pageRequest) {
        return ordersRepository.findByUserId(userId, pageRequest);
    }

    @Override
    public Page<Orders> findByUserIdAndId(Long userId, Long id, PageRequest pageRequest) {
        return ordersRepository.findByUserIdAndId(userId, id, pageRequest);
    }
    @Override
    public Page<Orders> findByStatusWithPaginationBySeller(Orders.OrderStatus status, Long userId, PageRequest pageRequest, Long id) {
        User user = userRepository.findById(userId).orElseThrow(()-> new DataNotFoundException("Cannot find user by this id"));

        Shop shop = shopRepository.findByUserId(userId);
        Page<Orders> ordersList = ordersRepository.findByStatusWithPaginationBySeller(status,shop.getId(),pageRequest,id);

        return ordersList;
    }

    @Override
    public Page<Orders> findByStatusWithPaginationByAdmin(Orders.OrderStatus status, PageRequest pageRequest, Long id) {

        Page<Orders> ordersList = ordersRepository.findByStatusWithPaginationByAdmin(status,pageRequest,id);

        return ordersList;
    }
    private String createMessage(Orders.OrderStatus status) {
        switch (status) {
            case PENDING:
                return "đang trong trạng thái chờ";
            case CONFIRMED:
                return "đã được xác nhận";
            case SHIPPED:
                return "đã được giao";
            case DELIVERED:
                return "đang được giao";
            case CANCELLED:
                return "đã bị huỷ";
            case PACKED:
                return "đã được đóng gói và đang trờ vận chuyển";
            case RETURNED:
                return "đã được trả lại";
            case COMPLETED:
                return "đã hoàn thành";
            default:
                return "";
        }
    }
}