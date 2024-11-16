package com.ptit.e_commerce_website_be.do_an_nhom.services.productAttributes;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.ProductAttributesDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.ProductAttributes;

import java.util.List;

public interface ProductAttributesService {
    ProductAttributesDTO createProductAttributes(ProductAttributesDTO productAttributesDTO, Long id, Long userId);
    ProductAttributesDTO getProductAttributesById(Long id);
    ProductAttributesDTO updateProductAttributes(ProductAttributesDTO productAttributesDTO, Long user);
    ProductAttributesDTO deleteProductAttributes(Long id, Long userId);
    List<ProductAttributes> getAllProductAttributes(Long idProduct);
}

