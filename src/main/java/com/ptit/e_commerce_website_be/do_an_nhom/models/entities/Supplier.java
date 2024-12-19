package com.ptit.e_commerce_website_be.do_an_nhom.models.entities;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.DetailSupplierDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@SqlResultSetMapping(
        name = "DetailSupplierMapping",
        classes = @ConstructorResult(
                targetClass = DetailSupplierDTO.class,
                columns = {
                        @ColumnResult(name = "name", type = String.class),
                        @ColumnResult(name = "country", type = String.class),
                        @ColumnResult(name = "province", type = String.class),
                        @ColumnResult(name = "district", type = String.class),
                        @ColumnResult(name = "commune", type = String.class),
                        @ColumnResult(name = "addressDetail", type = String.class)
                }
        )
)

@NamedNativeQuery(
        name = "Supplier.getDetailSupplierInfo",
        resultSetMapping = "DetailSupplierMapping",
        query = "SELECT w.name, a.country, a.province, a.district, a.commune, a.address_detail AS addressDetail " +
                "FROM supplier w " +
                "JOIN address a ON w.address_id = a.id " +
                "WHERE w.id = :supplierId " +
                "LIMIT 1"
)

@Entity
@Table(name = "supplier")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "address_id", nullable = false)
    private Long addressId;

    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime createdAt;

    @Column(name = "modified_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime modifiedAt;

    @Column(name = "is_delete")
    private Boolean isDelete;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        modifiedAt = LocalDateTime.now();
    }
}

