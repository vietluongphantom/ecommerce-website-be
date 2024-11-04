package com.ghtk.ecommercewebsite.services.shop;

import com.ghtk.ecommercewebsite.models.dtos.DetailShopInfoDTO;

import java.util.Optional;

public interface ShopService
{
    DetailShopInfoDTO getShopInfo(Long userId);
    DetailShopInfoDTO updateShopInfo(DetailShopInfoDTO detailShopInfoDTO, Long userId);
    DetailShopInfoDTO createInformationShop(DetailShopInfoDTO detailShopInfoDTO, Long userId);
    DetailShopInfoDTO getShopInfoById(Long id,Long userId);
}
