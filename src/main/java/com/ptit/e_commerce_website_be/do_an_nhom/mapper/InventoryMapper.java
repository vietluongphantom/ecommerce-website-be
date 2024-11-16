package com.ptit.e_commerce_website_be.do_an_nhom.mapper;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.InventoryDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Inventory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InventoryMapper {
    InventoryDTO toDTO(Inventory inventory);

    Inventory toEntity(InventoryDTO inventoryDTO);
}

