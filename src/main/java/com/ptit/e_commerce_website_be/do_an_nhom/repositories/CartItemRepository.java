package com.ptit.e_commerce_website_be.do_an_nhom.repositories;

import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Transactional
    void deleteByProductItemId(Long productItemId);

    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem ci WHERE ci.userId = :userId AND ci.productItemId = :productItemId")
    void deleteByUserIdAndProductId(Long userId, Long productItemId);

    List<CartItem> findByUserId(Long userId);
    void deleteByUserId(Long userId);

    CartItem findByProductItemIdAndUserId(Long productItemId, Long userId);

    Page<CartItem> findByUserId(Long userId, PageRequest pageRequest);
    Optional<CartItem> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT COUNT(*) FROM CartItem ci WHERE ci.userId = :userId")
    Long getQuantityCartItem(Long userId);

    List<CartItem> findByUserIdAndIdIn(Long userId, List<Long> cartItemIds);

    void deleteAllByIdIn(List<Long> cartItemIds);

}
