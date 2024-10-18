package com.ghtk.ecommercewebsite.models.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String note;


    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "total_price", precision = 12, scale = 2)
    private BigDecimal totalPrice;


    private boolean method;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime createdAt;

    @Column(name = "modified_at", columnDefinition = "DATETIME(6)")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime modifiedAt;

    @Column(name = "address")
    private String address;

    @Column(name = "address_detail")
    private String addressDetail;

    @Column(name = "receiver_phone")
    private String receiverPhone;

    @Column(name = "buyer")
    private String buyer;

    @Column(name = "shop_id")
    private Long shopId;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        modifiedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        modifiedAt = LocalDateTime.now();
    }
    public enum OrderStatus {
        PENDING,        // Đang chờ xử lý
        CONFIRMED,      // Đã xác nhận
        PACKED,         // Đã đóng gói
        SHIPPED,        // Đang giao hàng
        DELIVERED,      // Đã giao hàng
        CANCELLED,      // Đã hủy
        RETURNED,       // Đã trả hàng
        COMPLETED
    }
}
