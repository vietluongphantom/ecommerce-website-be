package com.ptit.e_commerce_website_be.do_an_nhom.repositories;

import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Voucher;
import com.ptit.e_commerce_website_be.do_an_nhom.models.enums.DiscountType;
import com.ptit.e_commerce_website_be.do_an_nhom.models.enums.RepeatType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;


@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    List<Voucher> findByNameContainingIgnoreCaseAndShopId(String name, Long shopId);
    List<Voucher> findByCouponCodeContainingIgnoreCaseAndShopId(String couponCode, Long shopId);
    List<Voucher> findByDiscountTypeAndShopId(DiscountType discountType, Long shopId);
    List<Voucher> findByTypeRepeatAndShopId(RepeatType typeRepeat, Long shopId);
    List<Voucher> findByIsActiveAndShopId(Boolean isActive, Long shopId);
    List<Voucher> findByIsPublicAndShopId(Boolean isPublic, Long shopId);
    List<Voucher> findByStartAtBetweenAndShopId(LocalDateTime startAt, LocalDateTime endAt, Long shopId);
    List<Voucher> findByExpiredAtBetweenAndShopId(LocalDateTime startAt, LocalDateTime endAt, Long shopId);
    List<Voucher> findByShopIdAndIsActiveAndIsPublic(Long shopId, Boolean isActive, Boolean isPublic);
    List<Voucher> findByShopId(Long shopId);
    Page<Voucher> findByShopId(Long shopId, Pageable pageable);
    List<Voucher> findByShopIdAndIsPublic(Long shopId, boolean isActive);
    List<Voucher> findByShopIdAndIsActiveAndIsPublic(Long shopId, boolean isActive, boolean isPublic);

    @Query("SELECT COUNT(*) FROM Voucher v WHERE v.shopId = ?1")
    Long getQuantityByShopId(Long shopId);
    List<Voucher> findAllByIsActiveTrueAndIsPublicFalse();
    List<Voucher> findAllByIsActiveTrue();
    // For the better version, not being used for now
    List<Voucher> findAllByIsActiveTrueAndExpiredAtAfter(LocalDateTime now);

    // Something
    @Modifying
    @Query("UPDATE Voucher v SET v.discountType = 'NONE' WHERE v.discountType IS NULL")
    void updateDiscountTypeToNone();
}
