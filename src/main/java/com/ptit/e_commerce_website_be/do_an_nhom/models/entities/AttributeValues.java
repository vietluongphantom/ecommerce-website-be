package com.ptit.e_commerce_website_be.do_an_nhom.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "attribute_values")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttributeValues {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="attribute_value_id")
    private Long attributeId;

    @Column(name = "value")
    private String value;

    @Column(name = "is_delete")
    private Boolean isDelete;
}

