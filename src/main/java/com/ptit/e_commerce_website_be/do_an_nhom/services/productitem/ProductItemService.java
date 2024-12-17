package com.ptit.e_commerce_website_be.do_an_nhom.services.productitem;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.DetailProductItemDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.ProductItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ProductItemService {
    DetailProductItemDTO createProductItem(DetailProductItemDTO detailProductItemDTO, Long userId);

    Page<Object> getAllProductItem(Long productId, Long userId, Pageable pageable);

    DetailProductItemDTO updateProductItem(DetailProductItemDTO detailProductItemDTO, Long userId);

    void deleteProductItem(Long id, Long userId);
    DetailProductItemDTO getProductItemById(Long id, Long userId);

    Map<String, Object> getProductItemByAttributesValues(Long id, List<Long> valuesIds);

    List<ProductItem> getListProductItemByProductId(Long id, Long userId);

    void rollbackQuantity(List<Long> listOrderId);
}

