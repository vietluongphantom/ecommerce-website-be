package com.ghtk.ecommercewebsite.models.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;


@Entity
@Table(name = "product_category")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "product_id", nullable = false)
    private Long productId;


    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "is_delete")
    private Boolean isDelete;
}
