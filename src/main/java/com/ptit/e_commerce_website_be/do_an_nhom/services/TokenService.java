package com.ptit.e_commerce_website_be.do_an_nhom.services;

import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Token;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.TokenRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository; // Repository giao tiếp với DB
    private final UserRepository userRepository;

    public Long getUserIdFromToken(String token) {
        Token tokenEntity = tokenRepository.findIDShopByToken(token)
                .orElseThrow(() -> new RuntimeException("Token không hợp lệ"));

        if (tokenEntity.isExpired() || tokenEntity.isRevoked()) {
            throw new RuntimeException("Token không còn hiệu lực");
        }

        return tokenEntity.getUser().getId();
    }

    public Long getShopIdByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        return user.getAddressId(); // Lấy shopID từ thông tin người dùng
    }
}

