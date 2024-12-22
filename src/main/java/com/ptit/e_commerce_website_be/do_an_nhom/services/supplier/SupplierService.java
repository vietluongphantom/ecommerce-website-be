package com.ptit.e_commerce_website_be.do_an_nhom.services.supplier;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.DetailSupplierDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface SupplierService {
    DetailSupplierDTO getSupplierInfo(Long id);
    DetailSupplierDTO createSupplier(DetailSupplierDTO detailSupplierDTO, Long userId);
    DetailSupplierDTO updateSupplierById(DetailSupplierDTO detailSupplierDTO, Long id, Long userId);
    void deleteSupplierById(Long id, Long userId);
    Page<Supplier> getAllSupplier(PageRequest pageRequest, Long userId, String name);
}

