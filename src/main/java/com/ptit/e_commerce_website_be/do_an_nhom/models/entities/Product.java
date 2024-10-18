package com.ghtk.ecommercewebsite.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
//@EntityListeners(ProductListener.class)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "name", length = 300)
    private String name;

    private String slug;
    private Integer status;

    @Column(name = "total_sold")
    private Long totalSold;

    @Column(name = "product_view")
    private Integer productView;


    @Column(name = "brand_id", nullable = false)
    private Long brandId;


    @Column(name = "shop_id", nullable = false)
    private Long shopId;


    @Column(name = "min_price")
    private BigDecimal minPrice;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        modifiedAt = LocalDateTime.now();
    }

    @Column(name = "is_delete")
    private Boolean isDelete;



    @ManyToMany(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH
    })

    @JoinTable(
            name = "product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @JsonIgnore
    private List<Category> categoryList;


    @Column(name = "thumbnail", length = 256)
    private String thumbnail;

    @PreUpdate
    protected void onUpdate() {
        modifiedAt = LocalDateTime.now();
    }
}
