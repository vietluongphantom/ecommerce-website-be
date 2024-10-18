package com.ghtk.ecommercewebsite.models.entities;

import com.ghtk.ecommercewebsite.models.dtos.DetailShopInfoDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NamedNativeQuery(
        name = "Shop.getDetailShopInfo",
        query = "SELECT s.name, s.mail, s.phone, a.country, a.province, a.district, a.commune, a.address_detail " +
                "FROM shop s LEFT JOIN address a ON s.address_id = a.id WHERE s.id = :shopId",
        resultSetMapping = "DetailShopInfoMapping"
)
@SqlResultSetMapping(
        name = "DetailShopInfoMapping",
        classes = @ConstructorResult(
                targetClass = DetailShopInfoDTO.class,
                columns = {
                        @ColumnResult(name = "name", type = String.class),
                        @ColumnResult(name = "mail", type = String.class),
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
@Table(name = "shop")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "name", length = 60)
    private String name;


    @Column(name = "mail", length = 90)
    private String mail;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "address_id")
    private Long addressId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

