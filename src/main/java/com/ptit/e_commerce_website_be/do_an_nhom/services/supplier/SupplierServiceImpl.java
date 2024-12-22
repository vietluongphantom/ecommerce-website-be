package com.ptit.e_commerce_website_be.do_an_nhom.services.supplier;


import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.DataNotFoundException;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.DetailSupplierDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Address;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Shop;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Supplier;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.AddressRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.ShopRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.SupplierRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepository supplierRepository;
    private final ShopRepository shopRepository;
    private final AddressRepository addressRepository;

    @Override
    public DetailSupplierDTO getSupplierInfo(Long id) {
        DetailSupplierDTO detailSupplierDTO = supplierRepository.findDetailBySupplierId(id);
        if (detailSupplierDTO == null){
            throw new DataNotFoundException("Cannot find detail Supplier information by id");
        }
        return detailSupplierDTO;
    }

    @Override
    @Transactional
    public DetailSupplierDTO createSupplier(DetailSupplierDTO detailSupplierDTO, Long userId){
        Shop shop = shopRepository.findShopByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find shop by id"));

        Address address = Address.builder()
                .addressDetail(detailSupplierDTO.getAddressDetail())
                .commune(detailSupplierDTO.getCommune())
                .country(detailSupplierDTO.getCountry())
                .district(detailSupplierDTO.getDistrict())
                .province(detailSupplierDTO.getProvince())
                .build();
        Address newAddress = addressRepository.save(address);

        Supplier supplier = Supplier.builder()
                .isDelete(Boolean.FALSE)
                .name(detailSupplierDTO.getName())
                .shopId(shop.getId())
                .addressId(newAddress.getId())
                .build();
        supplierRepository.save(supplier);
        return detailSupplierDTO;
    }

    @Override
    @Transactional
    public DetailSupplierDTO updateSupplierById(DetailSupplierDTO detailSupplierDTO, Long id, Long userId){
        Shop shop = shopRepository.findShopByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find shop by user id"));
        Long shopId = supplierRepository.findShopIdById(id);
        if (!shopId.equals(shop.getId())){
            throw  new AccessDeniedException("Cannot delete Supplier");
        }
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Supplier by id"));
        Address address = addressRepository.findById(supplier.getAddressId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find address by id"));
        supplier.setName(detailSupplierDTO.getName());
        supplierRepository.save(supplier);
        address.setDistrict(detailSupplierDTO.getDistrict());
        address.setAddressDetail(detailSupplierDTO.getAddressDetail());
        address.setCountry(detailSupplierDTO.getCountry());
        address.setCommune(detailSupplierDTO.getCommune());
        address.setProvince(detailSupplierDTO.getProvince());
        addressRepository.save(address);
        return detailSupplierDTO;
    }

    @Override
    @Transactional
    public void deleteSupplierById(Long id, Long userId){
        Shop shop = shopRepository.findShopByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find shop by user id"));
        Long shopId = supplierRepository.findShopIdById(id);
        if (!shopId.equals(shop.getId())){
            throw  new AccessDeniedException("Cannot delete Supplier");
        }
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(()-> new DataNotFoundException("Cannot find Supplier by id"));
        supplier.setIsDelete(Boolean.TRUE);
        supplierRepository.save(supplier);
    }

    @Override
    public Page<Supplier> getAllSupplier(PageRequest pageRequest, Long userId, String name){
        Shop shop = shopRepository.findShopByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find shop by user id"));
        Page<Supplier> supplierDtoList = supplierRepository.findByShopId(shop.getId(),  name, pageRequest);
        return supplierDtoList;
    }
}

