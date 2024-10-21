package com.ptit.e_commerce_website_be.do_an_nhom.services.brand;


import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.BrandDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.CategoryDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Brand;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


import java.util.List;
import java.util.Optional;

public interface IBrandService {
    Brand getBrandById(Long id) ;
    Brand createBrand(BrandDTO brandDTO, Long userId);
    public Page<Brand> getAllBrands(PageRequest pageRequest, String name) ;
    Brand updateBrand(Long categoryId, BrandDTO brandDTO, Long id) ;
    Brand deleteBrand(Long id, Long userId);
}
