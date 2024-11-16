package com.ptit.e_commerce_website_be.do_an_nhom.services.warehouse;


import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.DataNotFoundException;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.DetailWarehouseDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Address;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Shop;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Warehouse;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.AddressRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.ShopRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.WarehouseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService{
    private final WarehouseRepository warehouseRepository;
    private final ShopRepository shopRepository;
    private final AddressRepository addressRepository;

    @Override
    public DetailWarehouseDTO getWarehouseInfo(Long id) {
        DetailWarehouseDTO detailWarehouseDTO = warehouseRepository.findDetailByWarehouseId(id);
        if (detailWarehouseDTO == null){
            throw new DataNotFoundException("Cannot find detail warehouse information by id");
        }
        return detailWarehouseDTO;
    }

    @Override
    @Transactional
    public DetailWarehouseDTO createWarehouse(DetailWarehouseDTO detailWarehouseDTO,Long userId){
        Shop shop = shopRepository.findShopByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find shop by id"));

        Address address = Address.builder()
                .addressDetail(detailWarehouseDTO.getAddressDetail())
                .commune(detailWarehouseDTO.getCommune())
                .country(detailWarehouseDTO.getCountry())
                .district(detailWarehouseDTO.getDistrict())
                .province(detailWarehouseDTO.getProvince())
                .build();
        Address newAddress = addressRepository.save(address);

        Warehouse warehouse = Warehouse.builder()
                .isDelete(Boolean.FALSE)
                .name(detailWarehouseDTO.getName())
                .shopId(shop.getId())
                .addressId(newAddress.getId())
                .build();
        warehouseRepository.save(warehouse);
        return detailWarehouseDTO;
    }

    @Override
    @Transactional
    public DetailWarehouseDTO updateWarehouseById(DetailWarehouseDTO detailWarehouseDTO, Long id, Long userId){
        Shop shop = shopRepository.findShopByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find shop by user id"));
        Long shopId = warehouseRepository.findShopIdById(id);
        if (!shopId.equals(shop.getId())){
            throw  new AccessDeniedException("Cannot delete warehouse");
        }
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find warehouse by id"));
        Address address = addressRepository.findById(warehouse.getAddressId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find address by id"));
        warehouse.setName(detailWarehouseDTO.getName());
        warehouseRepository.save(warehouse);
        address.setDistrict(detailWarehouseDTO.getDistrict());
        address.setAddressDetail(detailWarehouseDTO.getAddressDetail());
        address.setCountry(detailWarehouseDTO.getCountry());
        address.setCommune(detailWarehouseDTO.getCommune());
        address.setProvince(detailWarehouseDTO.getProvince());
        addressRepository.save(address);
        return detailWarehouseDTO;
    }

    @Override
    @Transactional
    public void deleteWarehouseById(Long id, Long userId){
        Shop shop = shopRepository.findShopByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find shop by user id"));
        Long shopId = warehouseRepository.findShopIdById(id);
        if (!shopId.equals(shop.getId())){
            throw  new AccessDeniedException("Cannot delete warehouse");
        }
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(()-> new DataNotFoundException("Cannot find warehouse by id"));
        warehouse.setIsDelete(Boolean.TRUE);
        warehouseRepository.save(warehouse);
    }

    @Override
    public Page<Warehouse> getAllWarehouse(PageRequest pageRequest, Long userId, String name){
        Shop shop = shopRepository.findShopByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find shop by user id"));
        Page<Warehouse> warehouseDtoList = warehouseRepository.findByShopId(shop.getId(),  name, pageRequest);
        return warehouseDtoList;
    }
}

