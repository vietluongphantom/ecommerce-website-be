package com.ptit.e_commerce_website_be.do_an_nhom.models.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

