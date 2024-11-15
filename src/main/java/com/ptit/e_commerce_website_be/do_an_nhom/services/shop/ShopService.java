package com.ptit.e_commerce_website_be.do_an_nhom.services.shop;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.DetailShopInfoDTO;

import java.util.Optional;

public interface ShopService
{
    DetailShopInfoDTO getShopInfo(Long userId);
    DetailShopInfoDTO updateShopInfo(DetailShopInfoDTO detailShopInfoDTO, Long userId);
    DetailShopInfoDTO createInformationShop(DetailShopInfoDTO detailShopInfoDTO, Long userId);
    DetailShopInfoDTO getShopInfoById(Long id,Long userId);
}
