package com.ptit.e_commerce_website_be.do_an_nhom.mapper;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.ProductAttributesDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.ProductAttributes;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductAttributesMapper {
    ProductAttributesDTO toDTO(ProductAttributes productAttributes);
    ProductAttributes toEntity(ProductAttributesDTO productAttributesDTO);
}

