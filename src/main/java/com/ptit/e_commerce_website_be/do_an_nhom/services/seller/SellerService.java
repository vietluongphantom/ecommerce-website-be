package com.ptit.e_commerce_website_be.do_an_nhom.services.seller;

import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.DataNotFoundException;
import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.SellerAlreadyExistedException;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.*;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Seller;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Shop;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.LoginResponse;

import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public interface SellerService {
    User signUpSeller(SellerRegisterDto input);
    LoginResponse authenticateSellerAndGetLoginResponse(LoginUserDto loginUserDto);
    User getAuthenticatedSeller();
    DetailSellerInfoDTO getSellerInfo(Long useId);
    DetailSellerInfoDTO updateSellerInfo(DetailSellerInfoDTO detailSellerInfoDTO,Long useId);

    User viewDetailsOfAnSeller(Long id);

    Seller updateSellerInfo(SellerDTO sellerDTO);

    Shop updateShopInfo(Long userId, ShopDTO shopDTO);

    User signUpNewVersion(SellerRegisterDto sellerRegisterDto);

    User signUpNewestVersion(RegisterUserDto registerUserDto);

    Map<String, Long> getBasicInfo(Long userId);
}
