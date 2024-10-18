package com.ghtk.ecommercewebsite.models.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "product_item_attributes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductItemAttributes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String value;

    @Column(name = "product_attributes_id")
    private Long productAttributesId;
    @Column(name="attribute_value_id")
    private Long attributeValueId;

    @Column(name = "product_item_id", nullable = false)
    private Long productItemId;
}
