package com.ptit.e_commerce_website_be.do_an_nhom.services.voucher;

import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.DataNotFoundException;
import com.ptit.e_commerce_website_be.do_an_nhom.mapper.VoucherMapper;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.VoucherDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Role;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Shop;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Voucher;
import com.ptit.e_commerce_website_be.do_an_nhom.models.enums.DiscountType;
import com.ptit.e_commerce_website_be.do_an_nhom.models.enums.RepeatType;
import com.ptit.e_commerce_website_be.do_an_nhom.models.enums.RoleEnum;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.ShopRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.VoucherRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements IVoucherService {

    private final ShopRepository shopRepository;
    private final VoucherRepository voucherRepository;
    private final VoucherMapper voucherMapper;

    // Old scheduling version
    @Scheduled(fixedRate = 60000) // 1 minute
    @Transactional
    public void updateVouchersPublicStatus() {
        List<Voucher> vouchers = voucherRepository.findAllByIsActiveTrue();

        for (Voucher voucher : vouchers) {
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
            ZonedDateTime startAt = ZonedDateTime.of(voucher.getStartAt(), ZoneId.of("Asia/Ho_Chi_Minh"));
            ZonedDateTime expiredAt = ZonedDateTime.of(voucher.getExpiredAt(), ZoneId.of("Asia/Ho_Chi_Minh"));


            if (now.isAfter(startAt) && now.isBefore(expiredAt)) {
                if (!voucher.getIsPublic()) {
                    voucher.setIsPublic(true);
                    voucherRepository.save(voucher);
                }
            }

            if (now.isAfter(expiredAt)) {
                if (voucher.getIsPublic()) {
                    voucher.setIsPublic(false);
                    voucherRepository.save(voucher);
                }

                switch (voucher.getTypeRepeat()) {
                    case DAILY:
                        voucher.setStartAt(voucher.getStartAt().plusMinutes(1));
                        voucher.setExpiredAt(voucher.getExpiredAt().plusMinutes(1));
                        break;
                    case WEEKLY:
                        voucher.setStartAt(voucher.getStartAt().plusWeeks(1));
                        voucher.setExpiredAt(voucher.getExpiredAt().plusWeeks(1));
                        break;
                    case MONTHLY:
                        voucher.setStartAt(voucher.getStartAt().plusMonths(1));
                        voucher.setExpiredAt(voucher.getExpiredAt().plusMonths(1));
                        break;
                }
                voucherRepository.save(voucher);
            }
        }
    }


    // Old version
    @Override
    public Page<VoucherDTO> findAllVouchersByShopFromUser(Long userId, Pageable pageable){
        Shop shop = shopRepository.findShopByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("No shop found with this user"));
        Page<Voucher> voucherPage = voucherRepository.findByShopId(shop.getId(), pageable);
        return (Page<VoucherDTO>) voucherPage.getContent().stream()
                .map(voucherMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<VoucherDTO> findAllVouchersByShopFromSeller(Long userId, Pageable pageable){
        Shop shop = shopRepository.findShopByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("No shop found with this user"));

        List<VoucherDTO> vouchers = findAllVouchers(shop.getId());
        return convertListToPage(vouchers, pageable);
    }

    //    @Override
    public List<VoucherDTO> findAllVouchers(Long shopId) {
        List<Voucher> vouchers = voucherRepository.findByShopId(shopId);
        return vouchers.stream()
                .map(voucherMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<VoucherDTO> findVouchersByNameFromSeller(Long userId, String name, Pageable pageable) {
        Shop shop = shopRepository.findShopByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("No shop found with this user"));

        List<VoucherDTO> vouchers = findVouchersByName(name, shop.getId());
        return convertListToPage(vouchers, pageable);
    }

    public List<VoucherDTO> findVouchersByName(String name, Long shopId) {
        List<Voucher> vouchers = voucherRepository.findByNameContainingIgnoreCaseAndShopId(name, shopId);
        return vouchers.stream()
                .map(voucherMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<VoucherDTO> findVouchersByCouponCodeFromSeller(Long userId, String couponCode, Pageable pageable){
        Shop shop = shopRepository.findShopByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("No shop found with this user"));

        List<VoucherDTO> vouchers = findVouchersByCouponCode(couponCode, shop.getId());
        return convertListToPage(vouchers, pageable);
    }

    public List<VoucherDTO> findVouchersByCouponCode(String couponCode, Long shopId) {
        List<Voucher> vouchers = voucherRepository.findByCouponCodeContainingIgnoreCaseAndShopId(couponCode, shopId);
        return vouchers.stream()
                .map(voucherMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<VoucherDTO> findVouchersByDiscountTypeFromSeller(Long userId, DiscountType discountType, Pageable pageable){
        Shop shop = shopRepository.findShopByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("No shop found with this user"));

        List<VoucherDTO> vouchers = findVouchersByDiscountType(discountType, shop.getId());
        return convertListToPage(vouchers, pageable);
    }

    public List<VoucherDTO> findVouchersByDiscountType(DiscountType discountType, Long shopId) {
        List<Voucher> vouchers = voucherRepository.findByDiscountTypeAndShopId(discountType, shopId);
        return vouchers.stream()
                .map(voucherMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<VoucherDTO> findVouchersByRepeatTypeFromSeller(Long userId, RepeatType repeatType, Pageable pageable){
        Shop shop = shopRepository.findShopByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("No shop found with this user"));

        List<VoucherDTO> vouchers = findVouchersByTypeRepeat(repeatType, shop.getId());
        return convertListToPage(vouchers, pageable);
    }

    public List<VoucherDTO> findVouchersByTypeRepeat(RepeatType repeatType, Long shopId) {
        List<Voucher> vouchers = voucherRepository.findByTypeRepeatAndShopId(repeatType, shopId);
        return vouchers.stream()
                .map(voucherMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<VoucherDTO> findActiveVouchersFromSeller(Long userId, Boolean isActive, Pageable pageable){
        Shop shop = shopRepository.findShopByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("No shop found with this user"));

        List<VoucherDTO> vouchers = findActiveVouchers(isActive, shop.getId());
        return convertListToPage(vouchers, pageable);
    }

    public List<VoucherDTO> findActiveVouchers(Boolean isActive, Long shopId) {
        List<Voucher> vouchers = voucherRepository.findByIsActiveAndShopId(isActive, shopId);
        return vouchers.stream()
                .map(voucherMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<VoucherDTO> findPublicVouchersFromSeller(Long userId, Boolean isPublic, Pageable pageable){
        Shop shop = shopRepository.findShopByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("No shop found with this user"));

        List<VoucherDTO> vouchers = findPublicVouchers(isPublic, shop.getId());
        return convertListToPage(vouchers, pageable);
    }

    public List<VoucherDTO> findPublicVouchers(Boolean isPublic, Long shopId) {
        List<Voucher> vouchers = voucherRepository.findByIsPublicAndShopId(isPublic, shopId);
        return vouchers.stream()
                .map(voucherMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<VoucherDTO> searchVouchersByStartDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable){
        Shop shop = shopRepository.findShopByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("No shop found with this user"));

        List<VoucherDTO> vouchers = findVouchersByStartDateRange(startDate, endDate, shop.getId());
        return convertListToPage(vouchers, pageable);
    }

    public List<VoucherDTO> findVouchersByStartDateRange(LocalDateTime startDate, LocalDateTime endDate, Long shopId) {
        List<Voucher> vouchers = voucherRepository.findByStartAtBetweenAndShopId(startDate, endDate, shopId);
        return vouchers.stream()
                .map(voucherMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<VoucherDTO> searchVouchersByExpiredDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable){
        Shop shop = shopRepository.findShopByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("No shop found with this user"));

        List<VoucherDTO> vouchers = findVouchersByExpiredDateRange(startDate, endDate, shop.getId());
        return convertListToPage(vouchers, pageable);
    }

    public List<VoucherDTO> findVouchersByExpiredDateRange(LocalDateTime startDate, LocalDateTime endDate, Long shopId) {
        List<Voucher> vouchers = voucherRepository.findByExpiredAtBetweenAndShopId(startDate, endDate, shopId);
        return vouchers.stream()
                .map(voucherMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<VoucherDTO> findActiveAndPublicVouchersFromSeller(Long userId, Boolean isActive, Boolean isPublic, Pageable pageable) {
        Shop shop = shopRepository.findShopByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("No shop found with this user"));

        List<VoucherDTO> vouchers = findActiveAndPublicVouchers(isActive, isPublic, shop.getId());
        return convertListToPage(vouchers, pageable);
    }

    public List<VoucherDTO> findActiveAndPublicVouchers(Boolean isActive, Boolean isPublic, Long shopId) {
        List<Voucher> vouchers = voucherRepository.findByShopIdAndIsActiveAndIsPublic(shopId, isActive, isPublic);
        return vouchers.stream()
                .map(voucherMapper::toDTO)
                .collect(Collectors.toList());
    }

    private Page<VoucherDTO> convertListToPage(List<VoucherDTO> vouchers, Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        List<VoucherDTO> pagedVouchers;

        if (vouchers.size() < startItem) {
            pagedVouchers = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, vouchers.size());
            pagedVouchers = vouchers.subList(startItem, toIndex);
        }

        return new PageImpl<>(pagedVouchers, pageable, vouchers.size());
    }

    @Override
    public VoucherDTO createNewVoucher(VoucherDTO voucherDTO, Long userId){
        Shop shop = shopRepository.findShopByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("No shop found with this user"));

        Voucher voucher = voucherMapper.toEntity(voucherDTO);
        voucher.setShopId(shop.getId());
        voucherRepository.save(voucher);
        return voucherMapper.toDTO(voucher);
    }

    @Override
    public VoucherDTO getVoucherByIdByShopFromUser(Long voucherId, Long userId){
        Shop shop = shopRepository.findShopByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("No shop found with this user"));

        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new DataNotFoundException("Voucher not found with this id"));
        return voucherMapper.toDTO(voucher);
    }

    @Override
    public VoucherDTO updateVoucherByIdByShopFromUser(Long voucherId, Long userId, VoucherDTO voucherDTO){
        Shop shop = shopRepository.findShopByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("No shop found with this user"));
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new DataNotFoundException("Voucher not found with this id"));

        if (!voucher.getShopId().equals(shop.getId())) {
            throw new DataNotFoundException("You cannot edit this voucher");
        }

        updateVoucherFromVoucherDTO(voucherDTO, voucher);
        // Update is in this part
        voucherRepository.save(voucher);
        return voucherMapper.toDTO(voucher);
    }

    @Override
    public void deleteVoucherByIdByShopFromUser(Long voucherId, Long userId){
        Shop shop = shopRepository.findShopByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("No shop found with this user"));
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new DataNotFoundException("Voucher not found with this id"));

        if (!voucher.getShopId().equals(shop.getId())) {
            throw new DataNotFoundException("You cannot delete this voucher");
        }

        voucherRepository.deleteById(voucherId);
    }

    @Override
    public VoucherDTO setVoucherActive(Long voucherId, Long userId){
        return updateVoucherStatus(voucherId, userId, true, null);
    }

    @Override
    public VoucherDTO setVoucherInactive(Long voucherId, Long userId){
        return updateVoucherStatus(voucherId, userId, false, null);
    }

    @Override
    public VoucherDTO setVoucherPublic(Long voucherId, Long userId){
        return updateVoucherStatus(voucherId, userId, null, true);
    }

    @Override
    public VoucherDTO setVoucherPrivate(Long voucherId, Long userId){
        return updateVoucherStatus(voucherId, userId, null, false);
    }

    @Override
    public List<VoucherDTO> findAllVouchersByShopId(Long shopId) {
        LocalDateTime now = LocalDateTime.now();
        List<Voucher> vouchers = voucherRepository.findByShopIdAndIsActiveAndIsPublic(shopId, true, true);

        return vouchers.stream()
                .filter(voucher -> voucher.getExpiredAt().isAfter(now))
                .map(voucherMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public VoucherDTO switchVoucherStatus(Long voucherId, Long userId){
        Shop shop = shopRepository.findShopByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("No shop found with this user"));
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new DataNotFoundException("Voucher not found with this id"));

        if (!voucher.getShopId().equals(shop.getId())) {
            throw new DataNotFoundException("You cannot modify this voucher");
        }
        boolean newActiveStatus = !voucher.getIsActive();
        voucher.setIsActive(newActiveStatus);

        // Additional part
        if (newActiveStatus) {
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
            ZonedDateTime startAt = ZonedDateTime.of(voucher.getStartAt(), ZoneId.of("Asia/Ho_Chi_Minh"));
            ZonedDateTime expiredAt = ZonedDateTime.of(voucher.getExpiredAt(), ZoneId.of("Asia/Ho_Chi_Minh"));

            if (now.isAfter(startAt) && now.isBefore(expiredAt)) {
                voucher.setIsPublic(true);
            } else {
                voucher.setIsPublic(false);
            }
        } else {
            voucher.setIsPublic(false);
        }

        voucherRepository.save(voucher);
        return voucherMapper.toDTO(voucher);
    }

    private VoucherDTO updateVoucherStatus(Long voucherId, Long userId, Boolean isActive, Boolean isPublic) {
        Shop shop = shopRepository.findShopByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("No shop found with this user"));
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new DataNotFoundException("Voucher not found with this id"));

        if (!voucher.getShopId().equals(shop.getId())) {
            throw new DataNotFoundException("You cannot modify this voucher");
        }

        if (isActive != null) {
            voucher.setIsActive(isActive);
        }
        if (isPublic != null) {
            voucher.setIsPublic(isPublic);
        }

        voucherRepository.save(voucher);
        return voucherMapper.toDTO(voucher);
    }

    public static void updateVoucherFromVoucherDTO(VoucherDTO voucherDTO, Voucher voucher) {
        voucher.setCouponCode(voucherDTO.getCouponCode());
        voucher.setDiscountType(voucherDTO.getDiscountType());
        voucher.setDiscountValue(voucherDTO.getDiscountValue());
        voucher.setExpiredAt(voucherDTO.getExpiredAt());
        voucher.setIsActive(voucherDTO.isActive());
        voucher.setIsPublic(voucherDTO.isPublic());
        voucher.setMaximumDiscountValue(voucherDTO.getMaximumDiscountValue());
        voucher.setName(voucherDTO.getName());
        voucher.setStartAt(voucherDTO.getStartAt());
        voucher.setQuantity(voucherDTO.getQuantity());
        voucher.setMinimumQuantityNeeded(voucherDTO.getMinimumQuantityNeeded());
        voucher.setTypeRepeat(voucherDTO.getTypeRepeat());
    }


    @Override
    public List<Voucher> findAll() {
        return voucherRepository.findAll();
    }



    @Override
    public Optional<Voucher> findById(Long id) {
        return voucherRepository.findById(id);
    }

    @Override
    public Voucher save(Voucher voucher) {
        return voucherRepository.save(voucher);
    }

    @Override
    public void deleteById(Long id) {
        voucherRepository.deleteById(id);
    }

    @Override
    public List<Voucher> findByShopId(Long shopId) {
        return voucherRepository.findByShopId(shopId);
    }
}
