
package com.ptit.e_commerce_website_be.do_an_nhom.services.brand;
import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.DataNotFoundException;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.BrandDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.CategoryDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Brand;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Category;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements IBrandService {
    private final BrandRepository brandRepository;

    @Override
    public Brand getBrandById(Long id){
        return brandRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Brand not found"));
    }

    @Override
    @Transactional
    public Brand createBrand(BrandDTO brandDTO, Long userId) {
        Brand newBrand = Brand
                .builder()
                .isDelete(Boolean.FALSE)
                .name(brandDTO.getName())
                .status(brandDTO.getStatus())
                .build();
        return brandRepository.save(newBrand);
    }

    @Override
    public Page<Brand> getAllBrands(PageRequest pageRequest,  String name){
        return brandRepository.findAllBrand(name, pageRequest);
    }

    @Override
    @Transactional
    public Brand updateBrand(Long brandId, BrandDTO brandDTO, Long userId) {
        Brand existingBrand = getBrandById(brandId);
        existingBrand.setName(brandDTO.getName());
        existingBrand.setStatus(brandDTO.getStatus());
        brandRepository.save(existingBrand);
        return existingBrand;
    }

    @Override
    @Transactional
    public Brand deleteBrand(Long id, Long userId){
            Brand brand = getBrandById(id);
            brand.setIsDelete(Boolean.TRUE);
            brandRepository.save(brand);
        return brand;
    }
}
