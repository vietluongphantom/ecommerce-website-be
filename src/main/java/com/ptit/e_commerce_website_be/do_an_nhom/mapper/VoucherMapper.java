package com.ptit.e_commerce_website_be.do_an_nhom.mapper;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.VoucherDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Voucher;
import com.ptit.e_commerce_website_be.do_an_nhom.services.voucher.VoucherServiceImpl;
import org.springframework.stereotype.Component;


@Component
public class VoucherMapper {

    public Voucher toEntity(VoucherDTO dto) {
        if (dto == null) {
            return null;
        }

        Voucher voucher = new Voucher();
        voucher.setId(dto.getId());
        VoucherServiceImpl.updateVoucherFromVoucherDTO(dto, voucher);
        // Ignore createdAt and modifiedAt as per the requirement
        return voucher;
    }

    public VoucherDTO toDTO(Voucher voucher) {
        if (voucher == null) {
            return null;
        }

        VoucherDTO dto = new VoucherDTO();
        dto.setId(voucher.getId());
        dto.setCouponCode(voucher.getCouponCode());
        dto.setDiscountType(voucher.getDiscountType());
        dto.setDiscountValue(voucher.getDiscountValue());
        dto.setExpiredAt(voucher.getExpiredAt());
        dto.setActive(voucher.getIsActive());
        dto.setPublic(voucher.getIsPublic());
        dto.setMaximumDiscountValue(voucher.getMaximumDiscountValue());
        dto.setName(voucher.getName());
//        dto.setShopId(voucher.getShopId());
        dto.setStartAt(voucher.getStartAt());
        dto.setQuantity(voucher.getQuantity());
        dto.setMinimumQuantityNeeded(voucher.getMinimumQuantityNeeded());
        dto.setTypeRepeat(voucher.getTypeRepeat());
        // Ignore createdAt and modifiedAt as per the requirement
        return dto;
    }
}
