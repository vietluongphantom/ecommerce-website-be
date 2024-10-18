package com.ghtk.ecommercewebsite.models.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_status_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Orders.OrderStatus status;

    @Column(name = "changed_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime changedAt;

    @PrePersist
    protected void onCreate() {
        changedAt = LocalDateTime.now();
    }
}
