package com.ptit.e_commerce_website_be.do_an_nhom.controllers;

import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.DataNotFoundException;
import com.ptit.e_commerce_website_be.do_an_nhom.mapper.VoucherMapper;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.VoucherDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Voucher;
import com.ptit.e_commerce_website_be.do_an_nhom.models.enums.DiscountType;
import com.ptit.e_commerce_website_be.do_an_nhom.models.enums.RepeatType;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.CommonResult;
import com.ptit.e_commerce_website_be.do_an_nhom.services.voucher.IVoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final IVoucherService iVoucherService;
//    private final VoucherMapper voucherMapper;

    // User gets all vouchers from a shop, by shop's id
    @GetMapping("/getVouchers/{shopId}")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public CommonResult<List<VoucherDTO>> getAllVouchersByShopId(@PathVariable Long shopId) {
        return CommonResult.success(iVoucherService.findAllVouchersByShopId(shopId));
    }

    // From here is for sellers
    @GetMapping("/getAllVouchers")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<Page<VoucherDTO>> getAllVoucherByShopFromSeller(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success(iVoucherService.findAllVouchersByShopFromSeller(user.getId(), pageable));
    }

    @GetMapping("/search/name")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<Page<VoucherDTO>> getVouchersByNameFromSeller(
            @RequestParam("name") String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success(iVoucherService.findVouchersByNameFromSeller(user.getId(), name, pageable));
    }

    @GetMapping("/search/couponCode")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<Page<VoucherDTO>> getVouchersByCouponCodeFromSeller(
            @RequestParam("couponCode") String couponCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success(iVoucherService.findVouchersByCouponCodeFromSeller(user.getId(), couponCode, pageable));
    }

    @GetMapping("/search/discountType")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<Page<VoucherDTO>> getVouchersByDiscountTypeFromSeller(
            @RequestParam("discountType") DiscountType discountType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success(iVoucherService.findVouchersByDiscountTypeFromSeller(user.getId(), discountType, pageable));
    }

    @GetMapping("/search/repeatType")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<Page<VoucherDTO>> getVouchersByRepeatTypeFromSeller(
            @RequestParam("repeatType") RepeatType repeatType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success(iVoucherService.findVouchersByRepeatTypeFromSeller(user.getId(), repeatType, pageable));
    }

    @GetMapping("/search/active")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<Page<VoucherDTO>> getActiveVouchersFromSeller(
            @RequestParam("isActive") Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success(iVoucherService.findActiveVouchersFromSeller(user.getId(), isActive, pageable));
    }

    @GetMapping("/search/public")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<Page<VoucherDTO>> getPublicVouchersFromSeller(
            @RequestParam("isPublic") Boolean isPublic,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success(iVoucherService.findPublicVouchersFromSeller(user.getId(), isPublic, pageable));
    }

    @GetMapping("/search/startDate")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<Page<VoucherDTO>> searchVouchersByStartDateRange(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success(iVoucherService.searchVouchersByStartDateRange(user.getId(), startDate, endDate, pageable));
    }

    @GetMapping("/search/expiryDate")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<Page<VoucherDTO>> searchVouchersByExpiredDateRange(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success(iVoucherService.searchVouchersByExpiredDateRange(user.getId(), startDate, endDate, pageable));
    }

    @GetMapping("/search/activePublic")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<Page<VoucherDTO>> getActiveAndPublicVouchersFromSeller(
            @RequestParam("isActive") Boolean isActive,
            @RequestParam("isPublic") Boolean isPublic,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success(iVoucherService.findActiveAndPublicVouchersFromSeller(user.getId(), isActive, isPublic, pageable));
    }

    @PostMapping("/createNewVoucher")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<VoucherDTO> createNewVoucher(@Valid  @RequestBody VoucherDTO voucherDTO){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success(iVoucherService.createNewVoucher(voucherDTO, user.getId()));
    }

    @GetMapping("/getAllVouchers/{voucherId}")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<VoucherDTO> getVoucherById(@PathVariable Long voucherId){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success(iVoucherService.getVoucherByIdByShopFromUser(voucherId, user.getId()));
    }

    @PostMapping("/getAllVouchers/updateVoucher/{voucherId}")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<VoucherDTO> updateVoucher(
            @PathVariable Long voucherId,
            @RequestBody VoucherDTO voucherDTO) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success(iVoucherService.updateVoucherByIdByShopFromUser(voucherId, user.getId(), voucherDTO));
    }

    @DeleteMapping("/getAllVouchers/deleteVoucher/{voucherId}")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<String> deleteVoucher(@PathVariable Long voucherId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        iVoucherService.deleteVoucherByIdByShopFromUser(voucherId, user.getId());
        return CommonResult.success("Voucher with ID " + voucherId + " has been deleted.");
    }

    @PatchMapping("/getAllVouchers/setActive/{voucherId}")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<VoucherDTO> setVoucherActive(@PathVariable Long voucherId){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success(iVoucherService.setVoucherActive(voucherId, user.getId()));
    }

    @PatchMapping("/getAllVouchers/setInactive/{voucherId}")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<VoucherDTO> setVoucherInactive(@PathVariable Long voucherId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success(iVoucherService.setVoucherInactive(voucherId, user.getId()));
    }

    @PatchMapping("/getAllVouchers/switchStatus/{voucherId}")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<VoucherDTO> switchVoucherStatus(@PathVariable Long voucherId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return CommonResult.success(iVoucherService.switchVoucherStatus(voucherId, user.getId()));
    }
}
