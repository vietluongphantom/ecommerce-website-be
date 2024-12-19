package com.ptit.e_commerce_website_be.do_an_nhom.services.inventory;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.DetailInventoryDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.InventoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public interface InventoryService {
    InventoryDTO getInventoryById(Long Userid) ;

    Page<DetailInventoryDTO> getAllInventory(String warehouse, String skuCode, String name, Long userId, Pageable pageable);

    DetailInventoryDTO importWarehouse(DetailInventoryDTO detailInventoryDTO,Long userId) ;

    Page<DetailInventoryDTO> getListImport(String supplier,String location,String skuCode,String name ,String createdAt,Long userId, Pageable pageable);
    Page<DetailInventoryDTO>  getListExport(String supplier,String location,String skuCode,String name ,String createdAt, Long userId, Pageable pageable);

    ByteArrayInputStream getDataDownloaded(Long userId) throws IOException;

    ByteArrayInputStream getImportDataDownloaded(Long userId) throws IOException;

    List<DetailInventoryDTO> getAllListImport(Long userId) throws IOException;
}
