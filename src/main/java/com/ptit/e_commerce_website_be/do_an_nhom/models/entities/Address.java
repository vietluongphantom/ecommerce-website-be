package com.ghtk.ecommercewebsite.models.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "address")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "country", length = 45)
    private String country;

    @Column(name = "province", length = 45)
    private String province;


    @Column(name = "district", length = 45)
    private String district;

    @Column(name = "commune", length = 45)
    private String commune;

//    @Column(name = "location_id", nullable = false)
//    private Long locationId;

    @Column(name = "address_detail", nullable = false)
    private String addressDetail;

    @Column(name = "user_id")//, nullable = false)
    private Long userId;

//    @Column(name = "shop_id")
//    private Long shopId;
}
