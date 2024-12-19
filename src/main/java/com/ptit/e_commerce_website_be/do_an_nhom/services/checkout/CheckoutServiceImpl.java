package com.ptit.e_commerce_website_be.do_an_nhom.services.checkout;


import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.DataNotFoundException;
import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.QuantityExceededException;
import com.ptit.e_commerce_website_be.do_an_nhom.mapper.OrderMapper;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.OrdersDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.*;
import com.ptit.e_commerce_website_be.do_an_nhom.models.enums.DiscountType;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.*;
import com.ptit.e_commerce_website_be.do_an_nhom.services.address.AddressService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements ICheckoutService {

    private final CartItemRepository cartItemRepository;
    private final OrdersRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final VoucherRepository voucherRepository;
    private final ProductItemRepository productItemRepository;
    private final OrderMapper orderMapper;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final AddressService addressService;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    @Override
    public List<OrdersDTO> checkoutCart(Long userId, boolean method, String note, List<Long> selectedCartItems){
        validateCheckoutRequest(userId);
        // Lấy danh sách các CartItem được chọn
        List<CartItem> cartItemList = cartItemRepository.findByUserIdAndIdIn(userId, selectedCartItems);
        if (cartItemList.isEmpty()) throw new IllegalArgumentException("Không có sản phẩm nào được chọn để thanh toán");
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new DataNotFoundException("Cannot find user by this id"));
        // Nhóm các CartItem theo shopId
        Map<Long, List<CartItem>> cartItemsByShop = groupCartItemsByShopId(cartItemList);
        Address address = addressRepository.findByUserId(userId).orElseThrow(()-> new DataNotFoundException(""));
        String addressReceiver = address.getCommune() + ", " + address.getDistrict() + ", " + address.getProvince() + "," + address.getCountry();
        List<Orders> ordersList = new ArrayList<>();
        for (Map.Entry<Long, List<CartItem>> entry : cartItemsByShop.entrySet()) {
            Long shopId = entry.getKey();
            List<CartItem> itemsForShop = entry.getValue();
            // Tạo đơn hàng cho từng shopId
            Orders orders = createOrder(userId, method, note, shopId);
            for (CartItem cartItem : itemsForShop) {
                BigDecimal totalPrice = cartItem.getTotalPrice();
                int quantity = cartItem.getQuantity();
                BigDecimal unit = totalPrice.divide(BigDecimal.valueOf(quantity), BigDecimal.ROUND_HALF_UP);
                saveOrderItem(orders, cartItem, unit);
                minusQuantityProductItem(cartItem.getProductItemId(), cartItem.getQuantity());
            }
            // Cập nhật tổng giá của đơn hàng
            BigDecimal orderTotalPrice = calculateOrderTotalPrice(orders.getId());
            orders.setTotalPrice(orderTotalPrice);
            orders.setAddress(addressReceiver);
            orders.setAddressDetail(address.getAddressDetail());
            orders.setBuyer(user.getFullName());
            orders.setReceiverPhone(user.getPhone());
            orderRepository.save(orders);
            ordersList.add(orders);  // Thêm đơn hàng vào danh sách
        }
        // Xóa các CartItem đã được xử lý
        cartItemRepository.deleteAllByIdIn(selectedCartItems);
        // Chuyển đổi danh sách đơn hàng thành danh sách DTO
        return ordersList.stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    private BigDecimal calculateOrderTotalPrice(Long orderId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        return orderItems.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validateCheckoutRequest(Long userId){
        // Kiểm tra userId có tồn tại trong Address hay không
        boolean exists = addressService.isUserInAddress(userId);
        // Nếu không tồn tại, ném ngoại lệ với thông báo phù hợp
        if (!exists) {
            throw new IllegalArgumentException("User with id " + userId + " does not have a valid address");
        }
    }

    private Map<Long, List<CartItem>> groupCartItemsByShopId(List<CartItem> cartItemList) {
        return cartItemList.stream()
                .collect(Collectors.groupingBy(CartItem::getShopId));
    }

    private Orders createOrder(Long userId, boolean method, String note, Long shopId) {
        // Tạo đối tượng đơn hàng nhưng chưa lưu vào DB
        Orders orders = Orders.builder()
                .status(Orders.OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .note(note)
                .totalPrice(BigDecimal.ZERO)
                .method(method)
                .shopId(shopId)
                .userId(userId)
                .build();

        // Lưu đơn hàng vào cơ sở dữ liệu và đảm bảo order đã có ID
        orders = orderRepository.save(orders);

        // Ghi lại lịch sử trạng thái đơn hàng
        saveOrderStatusHistory(orders, orders.getStatus());

        return orders;
    }

    private void saveOrderStatusHistory(Orders order, Orders.OrderStatus status) {
        OrderStatusHistory history = OrderStatusHistory.builder()
                .message(createMessage(status))
                .isRead(Boolean.FALSE)
                .orderId(order.getId())
                .userId(order.getUserId())
                .status(status)
                .changedAt(LocalDateTime.now())
                .build();
        orderStatusHistoryRepository.save(history);
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


    @Transactional
    private void saveOrderItem(Orders orders, CartItem cartItem, BigDecimal unitPrice) {
        OrderItem orderItem = OrderItem.builder()
                .orderId(orders.getId())
                .productItemId(cartItem.getProductItemId())
                .quantity(cartItem.getQuantity())
                .unitPrice(unitPrice)
                .voucherId(cartItem.getVoucherId())
                .createdAt(LocalDateTime.now())
                .shopId(cartItem.getShopId())
                .build();
        orderItemRepository.save(orderItem);
    }

    @Transactional
    private  void minusQuantityProductItem(Long productItemId, Integer quantityProductItem){
        ProductItem productItem = productItemRepository.findById(productItemId).get();
        if (productItem.getQuantity() < quantityProductItem){
            throw new QuantityExceededException("Quantity of product item not enough");
        }
        productItem.setQuantity(productItem.getQuantity() - quantityProductItem);
        productItemRepository.save(productItem);
    }


    private void updateProductStock(ProductItem productItem, int quantity) {
        if (productItem.getQuantity() < quantity) throw new IllegalArgumentException("Số lượng sản phẩm không đủ");
        productItem.setQuantity(productItem.getQuantity() - quantity);
        productItemRepository.save(productItem);
    }


    private void validateDirectCheckout(Long productItemId, int quantity) {
        if (productItemId == null || quantity <= 0)
            throw new IllegalArgumentException("Invalid product item or quantity");
    }

    private BigDecimal calculateDirectDiscount(Long voucherId, int quantity, BigDecimal unitPrice) {
        BigDecimal discount = BigDecimal.ZERO;
        if (voucherId != null) {
            Voucher voucher = voucherRepository.findById(voucherId)
                    .orElseThrow(() -> new IllegalArgumentException("Voucher not found"));
            if (isVoucherApplicable(voucher, quantity)) {
                discount = applyVoucher(voucher, unitPrice, quantity);
            }
        }
        return discount;
    }


    private void saveDirectOrderItem(Orders orders, Long productItemId, int quantity, BigDecimal unitPrice, Long voucherId) {
        OrderItem orderItem = OrderItem.builder()
                .orderId(orders.getId())
                .productItemId(productItemId)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .voucherId(voucherId)
                .createdAt(LocalDateTime.now())
                .build();
        orderItemRepository.save(orderItem);
    }

    private boolean isVoucherApplicable(Voucher voucher, int quantity) {
        return voucher.getIsPublic() &&
                LocalDateTime.now().isBefore(voucher.getExpiredAt()) &&
                quantity >= voucher.getMinimumQuantityNeeded();
    }

    // Changed from voucher.getDiscountType() == 1 to voucher.getDiscountType().equals(DiscountType.PERCENT)
    private BigDecimal applyVoucher(Voucher voucher, BigDecimal unitPrice, int quantity) {
        BigDecimal discount = voucher.getDiscountType().equals(DiscountType.PERCENTAGE)
                ? unitPrice.multiply(voucher.getDiscountValue()).divide(BigDecimal.valueOf(100))
                : voucher.getDiscountValue();
        return discount.min(voucher.getMaximumDiscountValue()).multiply(BigDecimal.valueOf(quantity));
    }

    @Transactional
    @Override
    public BigDecimal calculateCartTotal(Long userId, List<Long> selectedCartItems) {
        // Kiểm tra danh sách sản phẩm đã chọn có tồn tại không
        List<CartItem> cartItemList = cartItemRepository.findByUserIdAndIdIn(userId, selectedCartItems);
        if (cartItemList.isEmpty()) throw new IllegalArgumentException("Không có sản phẩm nào được chọn");
        // Tính toán tổng giá tiền
        return processCartItemsForTotal(cartItemList);
    }

    @Override
    @Transactional
    public void setStatusOrder(Enum<Orders.OrderStatus> statusOrder, List<Long> listOrderId) {
        // Truy vấn danh sách đơn hàng từ cơ sở dữ liệu dựa trên listOrderId
        List<Orders> orders = orderRepository.findByIdIn(listOrderId);

        if (orders.isEmpty()) {
            throw new EntityNotFoundException("Không tìm thấy đơn hàng nào với danh sách ID đã cung cấp.");
        }

        // Cập nhật trạng thái của từng đơn hàng
        List<OrderStatusHistory> orderStatusHistoryList = new ArrayList<>();
        orders.forEach(order -> {
            order.setStatus((Orders.OrderStatus) statusOrder);
            OrderStatusHistory orderStatusHistory = OrderStatusHistory.builder()
                    .isRead(Boolean.FALSE)
                    .status((Orders.OrderStatus) statusOrder)
                    .message(createMessage((Orders.OrderStatus) statusOrder))
                    .userId(order.getUserId())
                    .orderId(order.getId())
                .build();
            orderStatusHistoryList.add(orderStatusHistory);
        });

        // Lưu các thay đổi vào cơ sở dữ liệu
        orderRepository.saveAll(orders);
        orderStatusHistoryRepository.saveAll(orderStatusHistoryList);
    }

    private BigDecimal processCartItemsForTotal(List<CartItem> cartItemList) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItem cartItem : cartItemList) {
            BigDecimal finalPrice = cartItem.getTotalPrice();
            totalPrice = totalPrice.add(finalPrice);
        }
        return totalPrice;
    }
}
