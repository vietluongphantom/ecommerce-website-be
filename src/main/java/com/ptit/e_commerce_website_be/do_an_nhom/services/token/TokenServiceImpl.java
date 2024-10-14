package com.ptit.e_commerce_website_be.do_an_nhom.services.token;


import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Token;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.TokenRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.services.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    @Value("${security.jwt.expiration-time}")
    private int expiration;

    private final TokenRepository tokenRepository;
    private final JwtService jwtService;


    @Transactional
    @Override
    public Token addToken(User user, String token) {
        long expirationInSeconds = expiration;
        LocalDateTime expirationDateTime = LocalDateTime.now().plusSeconds(expirationInSeconds/1000);
        // Tạo mới một token cho người dùng
        Token newToken = Token.builder()
                .user(user)
                .token(token)
                .revoked(false)
                .expired(false)
                .tokenType("Bearer")
                .expirationDate(expirationDateTime)
                .build();
        tokenRepository.save(newToken);
        return newToken;
    }
}

