package com.ptit.e_commerce_website_be.do_an_nhom.mapper;


import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.ProductDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductDTO dto) {
        if (dto == null) {
            return null;
        }

        Product product = new Product();
        product.setId(dto.getId());
        product.setMinPrice(dto.getMinPrice());
//        product.setThumbnail(dto.getThumbnailImg());
//        product.setIsDelete(dto.getIsDelete());
        product.setName(dto.getName());
        product.setIsDelete(dto.getIsDelete());
        product.setDescription(dto.getDescription());
        product.setStatus(dto.getStatus());
        product.setProductView(dto.getProductView());
        product.setTotalSold(dto.getTotalSold());
        product.setSlug(dto.getSlug());
        product.setBrandId(dto.getBrandId());
        product.setShopId(dto.getShopId());
        // Ignore createdAt and modifiedAt as per the requirement
        return product;
    }

    public ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
//        dto.setThumbnailImg(product.getThumbnail());
        dto.setDescription(product.getDescription());
        dto.setStatus(product.getStatus());
        dto.setIsDelete(product.getIsDelete());
        dto.setProductView(product.getProductView());
        dto.setTotalSold(product.getTotalSold());
        dto.setSlug(product.getSlug());
        dto.setBrandId(product.getBrandId());
        dto.setShopId(product.getShopId());
        // Ignore createdAt and modifiedAt as per the requirement
        return dto;
    }
}

