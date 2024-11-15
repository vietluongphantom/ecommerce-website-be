package com.ptit.e_commerce_website_be.do_an_nhom.repositories;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.DetailSellerInfoDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Seller;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {
    Optional<Seller> findById(Long userId);

    Optional<Seller> findByUserId(Long userId);

    @Query("SELECT s.shopId FROM Seller s WHERE s.userId = :userId")
    Long findShopIdByUserId(Long userId);

    @Query(
            "SELECT u FROM User u " +
            "JOIN u.roles r " +
            "WHERE u.email = :email AND r.name = 'SELLER'"
    )
    Optional<User> findUserWithSellerRoleByEmail(@Param("email") String email);

    @Query(
            "SELECT u FROM User u " +
                    "JOIN u.roles r " +
                    "WHERE u.id = :userId AND r.name = 'SELLER'"
    )
    Optional<User> findUserWithSellerRoleById(@Param("userId") Long userId);

    @Query(nativeQuery = true, name = "Seller.getDetailSellerInfo")
    Optional<DetailSellerInfoDTO> getDetailSellerInfo(Long userId);


}
