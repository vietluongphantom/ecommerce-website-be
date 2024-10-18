package com.ghtk.ecommercewebsite.models.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ghtk.ecommercewebsite.models.enums.DiscountType;
import com.ghtk.ecommercewebsite.models.enums.RepeatType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "voucher")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coupon_code", nullable = false, unique = true)
    private String couponCode;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "modified_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime modifiedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType;
//    private int discountType;

    @Column(name = "discount_value", precision = 12, scale = 2, nullable = false)
    private BigDecimal discountValue;


    @Column(name = "expired_at", nullable = false, columnDefinition = "DATETIME(6)")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiredAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;

    // Can be null
    @Column(name = "maximum_discount_value", precision = 12, scale = 2)
    private BigDecimal maximumDiscountValue;

    @Column(name = "name", length = 300, nullable = false)
    private String name;

    // Khóa ngoại
    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "start_at", nullable = false, columnDefinition = "DATETIME(6)")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startAt;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "minimum_quantity_needed", nullable = false)
    private Integer minimumQuantityNeeded;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_repeat", nullable = false)
    private RepeatType typeRepeat;
//    private boolean typeRepeat;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        modifiedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        modifiedAt = LocalDateTime.now();
    }

}