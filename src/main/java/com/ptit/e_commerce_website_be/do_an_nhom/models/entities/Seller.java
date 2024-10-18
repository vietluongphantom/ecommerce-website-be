package com.ghtk.ecommercewebsite.models.entities;

import com.ghtk.ecommercewebsite.models.dtos.DetailSellerInfoDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;



@NamedNativeQuery(
        name = "Seller.getDetailSellerInfo",
        resultSetMapping = "DetailSellerInfoMapping",
        query = "SELECT s.tax, s.cccd, u.email, u.gender  ,  u.full_name, u.phone, a.country, a.province, a.district, a.commune, a.address_detail " +
                "FROM seller s " +
                "JOIN users u ON s.user_id = u.id " +
                "LEFT JOIN address a ON a.user_id = u.id " +
                "WHERE s.user_id = :userId"
)

@SqlResultSetMapping(
        name = "DetailSellerInfoMapping",
        classes = @ConstructorResult(
                targetClass = DetailSellerInfoDTO.class,
                columns = {
                        @ColumnResult(name = "tax", type = String.class),
                        @ColumnResult(name = "cccd", type = String.class),
                        @ColumnResult(name = "email", type = String.class),
                        @ColumnResult(name = "gender", type = String.class),
                        @ColumnResult(name = "full_name", type = String.class),
                        @ColumnResult(name = "phone", type = String.class),
                        @ColumnResult(name = "country", type = String.class),
                        @ColumnResult(name = "province", type = String.class),
                        @ColumnResult(name = "district", type = String.class),
                        @ColumnResult(name = "commune", type = String.class),
                        @ColumnResult(name = "address_detail", type = String.class)
                }
        )
)





@Entity
@Table(name = "seller")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class  Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tax;

    private String cccd;

//    @Enumerated(EnumType.STRING)
//    @Column(name = "deliverytype", nullable = false)
//    private DeliveryType deliverytype;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "user_id", nullable = false)
    private Long userId;



    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

//    public enum DeliveryType {
//        STANDARD,
//        EXPRESS,
//        OVERNIGHT
//    }
}
