package com.ghtk.ecommercewebsite.models.entities;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;

@Entity
@Table(name = "rate")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "average_stars", precision = 12, scale = 2)
    private BigDecimal averageStars;

    private Long quantity;

    @Column(name = "product_id", nullable = false)
    private Long productId;

}
