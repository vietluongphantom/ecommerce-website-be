package com.ptit.e_commerce_website_be.do_an_nhom.services.warehouse;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.DetailWarehouseDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface WarehouseService {
    DetailWarehouseDTO getWarehouseInfo(Long id);
    DetailWarehouseDTO createWarehouse(DetailWarehouseDTO detailWarehouseDTO, Long userId);
    DetailWarehouseDTO updateWarehouseById(DetailWarehouseDTO detailWarehouseDTO, Long id, Long userId);
    void deleteWarehouseById(Long id, Long userId);
    Page<Warehouse> getAllWarehouse(PageRequest pageRequest, Long userId, String name);
}

