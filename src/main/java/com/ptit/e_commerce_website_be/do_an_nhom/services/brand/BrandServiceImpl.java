
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
                .description(brandDTO.getDescription())
                .name(brandDTO.getName())
                .icon(brandDTO.getIcon())
                .status(brandDTO.getStatus())
                .build();
        return brandRepository.save(newBrand);
    }

    @Override
    public Page<Brand> getAllBrands(PageRequest pageRequest,  String name){
        return brandRepository.findAllBrand(name, pageRequest);
    }

//    @Override
//    @Transactional
//    public Brand updateBrand(Long brandId, BrandDTO brandDTO, Long userId) {
//        Brand existingBrand = getBrandById(brandId);
//        existingBrand.setName(brandDTO.getName());
//        existingBrand.setStatus(brandDTO.getStatus());
//        brandRepository.save(existingBrand);
//        return existingBrand;
//    }

    @Transactional
    public Brand updateBrand(Long brandId, BrandDTO brandDTO, Long userId) {
        // Lấy brand hiện có
        Brand existingBrand = getBrandById(brandId);

        // Kiểm tra nếu thương hiệu đã bị xóa (isDelete = true)
        if (Boolean.TRUE.equals(existingBrand.getIsDelete())) {
            throw new IllegalArgumentException("Không thể cập nhật thương hiệu đã bị xóa.");
        }

        // Kiểm tra xem có thương hiệu khác với cùng tên tồn tại không
        boolean isDuplicateName = brandRepository.existsByNameAndIdNotAndIsDelete(
                brandDTO.getName(),
                brandId,
                false // chỉ kiểm tra thương hiệu chưa bị xóa
        );

        if (isDuplicateName) {
            throw new IllegalArgumentException("Tên thương hiệu đã tồn tại.");
        }

        // Cập nhật thông tin thương hiệu nếu không có lỗi
        existingBrand.setName(brandDTO.getName());
        existingBrand.setStatus(brandDTO.getStatus());
        return brandRepository.save(existingBrand);
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
