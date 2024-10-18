package com.ghtk.ecommercewebsite.models.entities;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "link")
    private String link;

    private Long size;

    private int type;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "comment_id")
    private Long commentId;

    @Column(name = "users_id")
    private Long usersId;

    private String name;

    @Column(name = "brand_id")
    private Long brandId;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
