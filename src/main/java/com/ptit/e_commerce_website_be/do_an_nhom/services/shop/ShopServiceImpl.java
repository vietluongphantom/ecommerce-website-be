package com.ghtk.ecommercewebsite.services.shop;

import com.ghtk.ecommercewebsite.exceptions.DataNotFoundException;
import com.ghtk.ecommercewebsite.models.dtos.DetailSellerInfoDTO;
import com.ghtk.ecommercewebsite.models.dtos.DetailShopInfoDTO;
import com.ghtk.ecommercewebsite.models.entities.Address;
import com.ghtk.ecommercewebsite.models.entities.Seller;
import com.ghtk.ecommercewebsite.models.entities.Shop;
import com.ghtk.ecommercewebsite.repositories.AddressRepository;
import com.ghtk.ecommercewebsite.repositories.SellerRepository;
import com.ghtk.ecommercewebsite.repositories.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService{
    private  final ShopRepository shopRepository;
    private  final SellerRepository sellerRepository;
    private  final AddressRepository addressRepository;
    @Override
    public DetailShopInfoDTO getShopInfo(Long userId){
        Long shopId = sellerRepository.findShopIdByUserId(userId);
        if(shopId == null){
            throw new DataNotFoundException("Cannot not found shop id by userId");
        }
        DetailShopInfoDTO detailShopInfoDTO = shopRepository.getDetailShopInfo(shopId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find shop information"));
        return detailShopInfoDTO;
    }

    @Override
    @Transactional
    public DetailShopInfoDTO updateShopInfo(DetailShopInfoDTO detailShopInfoDTO ,Long userId){
        Shop shop = shopRepository.findShopByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("shop not found"));
        shop.setMail(detailShopInfoDTO.getMail());
        shop.setName(detailShopInfoDTO.getName());
        shop.setPhone(detailShopInfoDTO.getPhone());

        Address address = addressRepository.findByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("address not found"));
        address.setAddressDetail(detailShopInfoDTO.getAddressDetail());
        address.setCountry(detailShopInfoDTO.getCountry());
        address.setCommune(detailShopInfoDTO.getCommune());
        address.setProvince(detailShopInfoDTO.getProvince());
        address.setDistrict(detailShopInfoDTO.getDistrict());

        shopRepository.save(shop);
        addressRepository.save(address);
        return detailShopInfoDTO;
    }

    @Override
    @Transactional
    public DetailShopInfoDTO createInformationShop(DetailShopInfoDTO detailShopInfoDTO, Long userId){

        Address address = Address.builder()
                .addressDetail(detailShopInfoDTO.getAddressDetail())
                .country(detailShopInfoDTO.getCountry())
                .district(detailShopInfoDTO.getDistrict())
                .province(detailShopInfoDTO.getProvince())
                .commune(detailShopInfoDTO.getCommune())
                .build();

        Address newAddress = addressRepository.save(address);

        Shop shop = Shop.builder()
                .userId(userId)
                .name(detailShopInfoDTO.getName())
                .mail(detailShopInfoDTO.getMail())
                .phone(detailShopInfoDTO.getPhone())
                .addressId(newAddress.getId())
                .build();

        Shop newShop = shopRepository.save(shop);
        Seller seller = sellerRepository.findByUserId(userId)
                .orElseThrow(()-> new DataNotFoundException("Cannot found seller by user Id"));
        seller.setShopId(newShop.getId());
        sellerRepository.save(seller);

        return detailShopInfoDTO;
    }


    @Override
    public DetailShopInfoDTO getShopInfoById(Long id,Long userId){
        Shop shopId = shopRepository.findById(id)
                .orElseThrow(()-> new DataNotFoundException("Cannot find shop id by id"));
        if(shopId == null){
            throw new DataNotFoundException("Cannot not found shop id by userId");
        }
        DetailShopInfoDTO detailShopInfoDTO = shopRepository.getDetailShopInfo(shopId.getId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find shop information"));
        return detailShopInfoDTO;
    }
}
