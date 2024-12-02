package com.ptit.e_commerce_website_be.do_an_nhom.services.product;


import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.ProductDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Product;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
public interface IProductService {

    List<Product> findAll();

    Optional<Product> findById(Long id);

    Product save(ProductDTO productDTO, Long userId);

    void deleteById(Long id);

    List<Product> findByBrandId(Long brandId);
    //    void deleteBrandById(Long id);
    List<Product> searchProductsByName(String keyword); // Phương thức tìm kiếm

    List<Product> searchProductsByDes(String keyword); // Phương thức tìm kiếm

    List<Product> getAllProducts();

    Page<ProductResponse> searchProducts (List<Long> categoryIds, long categoryCount, List<Long> brandIds, String keyword, Long fromPrice , Long toPrice, Float
            rate , PageRequest pageRequest);

    ProductResponse getProductById(Long id);

    Page<ProductResponse> searchProductsSeller (List<Long> categoryIds, long categoryCount, List<Long> brandIds, String keyword,Long userId, PageRequest pageRequest);

    Product updateProductById(Long id,ProductDTO productDTO, Long userId);

    Product insertAProduct(ProductDTO productDTO);

    ByteArrayInputStream getProductDataDownloaded(Long userId) throws IOException;
}

