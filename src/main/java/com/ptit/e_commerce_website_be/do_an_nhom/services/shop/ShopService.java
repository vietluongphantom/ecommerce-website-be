package com.ptit.e_commerce_website_be.do_an_nhom.services.shop;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.DetailShopInfoDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Shop;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.ShopRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Shop;

import java.util.Optional;

public interface ShopService
{
    DetailShopInfoDTO getShopInfo(Long userId);
    DetailShopInfoDTO updateShopInfo(DetailShopInfoDTO detailShopInfoDTO, Long userId);
    DetailShopInfoDTO createInformationShop(DetailShopInfoDTO detailShopInfoDTO, Long userId);
    DetailShopInfoDTO getShopInfoById(Long id,Long userId);
//    Long getCurrentShopId(Long userId);
    Shop getShopInfoByUserId(Long id);
}
