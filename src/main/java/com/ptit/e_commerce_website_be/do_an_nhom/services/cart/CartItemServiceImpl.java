package com.ptit.e_commerce_website_be.do_an_nhom.services.cart;

import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.DataNotFoundException;
import com.ptit.e_commerce_website_be.do_an_nhom.mapper.CartItemMapper;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.CartItemDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.CartItem;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.ProductItem;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Voucher;
import com.ptit.e_commerce_website_be.do_an_nhom.models.enums.DiscountType;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.CartItemRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.ProductItemRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements ICartItemService {

    private final CartItemRepository cartItemRepository;

    private final VoucherRepository voucherRepository;

    private final CartItemMapper cartMapper;

    private final ProductItemRepository productItemRepository;

    @Override
    public CartItem getCartItemById(Long id){
        return cartItemRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cart item not found"));
    }

    @Override
    public void createCartItem(CartItemDTO cartItemDTO, Long userId) {
        // Lấy thông tin sản phẩm từ cơ sở dữ liệu
        ProductItem productItem = productItemRepository.findById(cartItemDTO.getProductItemId())
                .orElseThrow(() -> new DataNotFoundException("Product item not found"));

        // Kiểm tra số lượng sản phẩm có sẵn
        if (productItem.getQuantity() < cartItemDTO.getQuantity()) {
            throw new IllegalArgumentException("Not enough quantity available");
        }

        // Cập nhật số lượng sản phẩm trong giỏ hàng nếu đã có
        CartItem existingCartItem = cartItemRepository.findByProductItemIdAndUserId(cartItemDTO.getProductItemId(), userId);
        if (existingCartItem != null) {
            // Nếu sản phẩm đã có trong giỏ hàng, cập nhật số lượng
            int newQuantity = existingCartItem.getQuantity() + cartItemDTO.getQuantity();

            // Kiểm tra số lượng mới có vượt quá số lượng sản phẩm hiện có hay không
            if (productItem.getQuantity() < newQuantity) {
                throw new IllegalArgumentException("Not enough quantity available for update");
            }

            // Cập nhật số lượng và tổng giá
            existingCartItem.setQuantity(newQuantity);
            BigDecimal finalPrice = productItem.getPrice().multiply(BigDecimal.valueOf(newQuantity));
            existingCartItem.setTotalPrice(finalPrice);
            cartItemRepository.save(existingCartItem);
        } else {
            // Nếu sản phẩm chưa có trong giỏ hàng, tạo mới
            CartItem cartItem = cartMapper.toEntity(cartItemDTO);
            // Lấy shopId và gán cho cartItem
            // Lấy shopId và gán cho cartItem
            Long shopId = productItemRepository.findShopIdByProductItemId(cartItemDTO.getProductItemId());
            cartItem.setUserId(userId);
            cartItem.setShopId(shopId);

            // Tính toán tổng giá dựa trên số lượng và giá của sản phẩm
            BigDecimal unitPrice = productItem.getPrice();
            BigDecimal finalPrice = unitPrice.multiply(BigDecimal.valueOf(cartItemDTO.getQuantity()));
            cartItem.setTotalPrice(finalPrice);

            // Lưu mới cartItem vào cơ sở dữ liệu
            cartItemRepository.save(cartItem);
        }

        // Giảm số lượng sản phẩm có sẵn trong productItem
//        productItem.setQuantity(productItem.getQuantity() - cartItemDTO.getQuantity());

        productItemRepository.save(productItem);
    }




    private boolean isVoucherApplicable(Voucher voucher, int quantity) {
        return voucher.getIsPublic() &&
                LocalDateTime.now().isBefore(voucher.getExpiredAt()) &&
                quantity >= voucher.getMinimumQuantityNeeded();
    }

    private BigDecimal applyVoucher(Voucher voucher, BigDecimal unitPrice, int quantity) {
        // Tính tổng giá trị giảm giá ban đầu là 0
        BigDecimal discount = BigDecimal.ZERO;

        // Kiểm tra loại giảm giá
        if (voucher.getDiscountType().equals(DiscountType.PERCENTAGE)) {
            // Giảm theo phần trăm dựa trên đơn giá và số lượng
            discount = unitPrice.multiply(BigDecimal.valueOf(quantity))
                    .multiply(voucher.getDiscountValue())
                    .divide(BigDecimal.valueOf(100));
        } else if (voucher.getDiscountType().equals(DiscountType.FIXEDAMOUNT)) {
            // Giảm theo giá trị cố định, nhân với số lượng
            discount = voucher.getDiscountValue().multiply(BigDecimal.valueOf(quantity));
        }

        // Nếu có giá trị giảm giá tối đa
        if (voucher.getMaximumDiscountValue() != null) {
            discount = discount.min(voucher.getMaximumDiscountValue().multiply(BigDecimal.valueOf(quantity)));
        }

        return discount;
    }


    @Override
    public Page<CartItem> getAllCartItems(PageRequest pageRequest, Long userId) {
        return cartItemRepository.findByUserId(userId, pageRequest);
    }

    @Override
    public void deleteCartItem(Long id, Long userId){
        Optional<CartItem> cartItemOptional = cartItemRepository.findByIdAndUserId(id, userId);

        if (cartItemOptional.isPresent()) {
            CartItem cartItem = cartItemOptional.get();

            // Lấy ProductItem liên quan tới CartItem
            ProductItem productItem = productItemRepository.findById(cartItem.getProductItemId())
                    .orElseThrow(() -> new DataNotFoundException("Product item not found"));

            // Trả lại số lượng sản phẩm vào ProductItem
            productItem.setQuantity(productItem.getQuantity() + cartItem.getQuantity());
            productItemRepository.save(productItem);  // Lưu lại thay đổi số lượng

            // Xóa CartItem
            cartItemRepository.delete(cartItem);
        } else throw new DataNotFoundException("Cart item not found or does not belong to the user");
    }

    @Override
    public CartItem updateCartItemQuantity(Long cartItemId, int quantity, Long userId){
        // Lấy thông tin giỏ hàng dựa trên cartItemId và userId
        CartItem cartItem = cartItemRepository.findByIdAndUserId(cartItemId, userId)
                .orElseThrow(() -> new DataNotFoundException("Cart item not found"));

        // Cập nhật số lượng sản phẩm
        ProductItem productItem = productItemRepository.findById(cartItem.getProductItemId())
                .orElseThrow(() -> new IllegalArgumentException("Product item not found"));

        // Kiểm tra số lượng sản phẩm có đủ không
        if (productItem.getQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough quantity available");
        }

        // Cập nhật số lượng trong productItem
        int currentQuantityInCart = cartItem.getQuantity();
        int quantityDifference = quantity - currentQuantityInCart;
        if (productItem.getQuantity() < quantityDifference) {
            throw new IllegalArgumentException("Not enough quantity available");
        }
        productItem.setQuantity(productItem.getQuantity() - quantityDifference);

        // Lưu thông tin productItem đã cập nhật
        productItemRepository.save(productItem);

        // Cập nhật số lượng trong cartItem
        cartItem.setQuantity(quantity);

        // Tính toán lại giá cuối cùng
        BigDecimal unitPrice = productItem.getPrice();
        BigDecimal discount = BigDecimal.ZERO;
        if (cartItem.getVoucherId() != null) {
            discount = applyVoucher(voucherRepository.findById(cartItem.getVoucherId())
                    .orElseThrow(), unitPrice, quantity);
        }
        BigDecimal finalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity)).subtract(discount);
        cartItem.setTotalPrice(finalPrice);

        // Lưu thay đổi
        return cartItemRepository.save(cartItem);
    }


    @Override
    public Long getQuantityCartItem(Long userId) {
        return cartItemRepository.getQuantityCartItem(userId);
    }

    @Override
    @Transactional
    public void applyVoucherToCartItem(Long cartItemId, Long voucherId, Long userId) {
        CartItem cartItem = cartItemRepository.findByIdAndUserId(cartItemId, userId)
                .orElseThrow(() -> new IllegalArgumentException("CartItem không tồn tại hoặc không thuộc về người dùng"));

        ProductItem productItem = productItemRepository.findById(cartItem.getProductItemId())
                .orElseThrow(() -> new IllegalArgumentException("Product item not found"));
        if (voucherId != null) {
            Voucher voucher = voucherRepository.findById(voucherId)
                    .orElseThrow(() -> new IllegalArgumentException("Voucher không tồn tại"));
            // Kiểm tra điều kiện của voucher
            if (!voucher.getIsPublic() || LocalDateTime.now().isAfter(voucher.getExpiredAt()))
                throw new IllegalArgumentException("Voucher không hợp lệ hoặc đã hết hạn");
            BigDecimal unitPrice = productItem.getPrice();
            BigDecimal discount = applyVoucher(voucher, unitPrice, cartItem.getQuantity());
            BigDecimal finalPrice = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity())).subtract(discount);
            cartItem.setVoucherId(voucherId);
            cartItem.setTotalPrice(finalPrice);
            cartItemRepository.save(cartItem);
        } else {
            BigDecimal unitPrice = productItem.getPrice();
            BigDecimal finalPrice = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            cartItem.setTotalPrice(finalPrice);
            cartItemRepository.save(cartItem);
            cartItem.setVoucherId(null);
        }
    }
}
