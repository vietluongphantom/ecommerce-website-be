package com.ghtk.ecommercewebsite.models.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "statistic")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Statistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "profit", precision = 17, scale = 2)
    private BigDecimal profit;

    @Column(name = "sales", precision = 17, scale = 2)
    private BigDecimal sales;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
