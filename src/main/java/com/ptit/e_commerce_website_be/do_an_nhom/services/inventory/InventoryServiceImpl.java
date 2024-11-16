package com.ptit.e_commerce_website_be.do_an_nhom.services.inventory;


import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.DataNotFoundException;
import com.ptit.e_commerce_website_be.do_an_nhom.mapper.InventoryMapper;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.DetailInventoryDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.InventoryDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Inventory;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.ProductItem;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Supply;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.InventoryRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.ProductItemRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.SellerRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.SupplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService{
    private final InventoryRepository inventoryRepository;
    private final SellerRepository sellerRepository;
    private final InventoryMapper inventoryMapper;
    private final ProductItemRepository productItemRepository;
    private final SupplyRepository supplyRepository;


    @Override
    public InventoryDTO getInventoryById(Long id){
        Inventory inventory =  inventoryRepository.findById(id)
                .orElseThrow(()->new DataNotFoundException("Cannot find inventory by id"));
        return inventoryMapper.toDTO(inventory);
    }

    @Override
    public Page<DetailInventoryDTO> getAllInventory(String warehouse, String skuCode, String name, Long userId, Pageable pageable){
        Long shopId = sellerRepository.findShopIdByUserId(userId);
        if (shopId == null){
            throw  new DataNotFoundException("Cannot find shopId by userId");
        }
        List<DetailInventoryDTO> detailInventoryDTOList = inventoryRepository.getAllInventory(warehouse, skuCode, name, shopId, pageable.getPageSize(), pageable.getOffset());
        int totalItems = inventoryRepository.countAllInventory(warehouse, skuCode, name, shopId);
        return new PageImpl<>(detailInventoryDTOList, pageable, totalItems);
    }

    @Override
    @Transactional
    public DetailInventoryDTO importWarehouse(DetailInventoryDTO detailInventoryDTO, Long userId){
        ProductItem productItem = productItemRepository.findBySkuCode(detailInventoryDTO.getSkuCode(), detailInventoryDTO.getProductId());
        if(productItem == null){
            throw new DataNotFoundException("Cannot found productItem");
        }
        int quantity  = productItem.getQuantity()==null?0: productItem.getQuantity();
        productItem.setQuantity(quantity + detailInventoryDTO.getQuantity());
        productItemRepository.save(productItem);
        Supply supply = Supply.builder()
                .quantity(detailInventoryDTO.getQuantity())
                .warehouseId(detailInventoryDTO.getWarehouseId())
                .productItemId(productItem.getId())
                .supplier(detailInventoryDTO.getSupplier())
                .location(detailInventoryDTO.getLocation())
                .status(Boolean.TRUE)
                .build();
        supplyRepository.save(supply);

        Inventory inventory = inventoryRepository.findByProductItemIdAndWarehouseId(productItem.getId(), detailInventoryDTO.getWarehouseId());
        if(inventory == null){
            Inventory newInventory = Inventory.builder()
                    .warehouseId(detailInventoryDTO.getWarehouseId())
                    .quantity(detailInventoryDTO.getQuantity())
                    .productItemId(productItem.getId())
                    .build();
            inventoryRepository.save(newInventory);

        }else {
            inventory.setQuantity(detailInventoryDTO.getQuantity() + inventory.getQuantity());
            inventoryRepository.save(inventory);
        }

        return detailInventoryDTO;
    }

    @Override
    public Page<DetailInventoryDTO> getListImport(String warehouse,String supplier,String location,String skuCode,String name ,String createdAt, Long userId, Pageable pageable) {
        Long shopId = sellerRepository.findShopIdByUserId(userId);
        List<DetailInventoryDTO> detailInventoryDTOList = supplyRepository.getAllImport(warehouse,supplier,location,skuCode, name , createdAt,shopId, pageable.getPageSize(), pageable.getOffset());
        int totalItems = supplyRepository.countAllImport(warehouse,supplier,location,skuCode, name , createdAt,shopId);
        return new PageImpl<>(detailInventoryDTOList, pageable, totalItems);
    }

    @Override
    public Page<DetailInventoryDTO> getListExport(String warehouse, String  supplier,String location,String skuCode,String  name ,String createdAt, Long userId, Pageable pageable){
        Long shopId = sellerRepository.findShopIdByUserId(userId);
        List<DetailInventoryDTO> detailInventoryDTOList = supplyRepository.getAllExport(warehouse,supplier,location,skuCode, name , createdAt,shopId, pageable.getPageSize(), pageable.getOffset());
        int totalItems = supplyRepository.countAllExport(warehouse,supplier,location,skuCode, name , createdAt,shopId);
        return new PageImpl<>(detailInventoryDTOList, pageable, totalItems);
    }
}

