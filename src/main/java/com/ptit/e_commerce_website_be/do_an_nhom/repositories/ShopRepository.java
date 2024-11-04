package com.ghtk.ecommercewebsite.repositories;

import com.ghtk.ecommercewebsite.models.dtos.DetailSellerInfoDTO;
import com.ghtk.ecommercewebsite.models.dtos.DetailShopInfoDTO;
import com.ghtk.ecommercewebsite.models.entities.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    Shop findByUserId(Long userId);

    @Query(nativeQuery = true, name = "Shop.getDetailShopInfo")
    Optional<DetailShopInfoDTO> getDetailShopInfo(@Param("shopId") Long shopId);

    @Query(nativeQuery = true, value = "SELECT s.* " +
            "FROM shop s " +
            "INNER JOIN seller sl ON s.id = sl.shop_id " +
            "INNER JOIN users u ON sl.user_id = u.id " +
            "WHERE u.id = :userId")
    Optional<Shop> findShopByUserId(@Param("userId") Long userId);

}
