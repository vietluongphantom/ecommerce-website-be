package com.ptit.e_commerce_website_be.do_an_nhom.services.blacklisttoken;

import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.BlacklistToken;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.BlackListTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class BlacklistTokenServiceImpl implements BlacklistTokenService{

    @Value("${security.jwt.expiration-time}")
    private int expiration;

    private final BlackListTokenRepository blackListTokenRepository;
    @Override
    @Transactional
    public void addToBlackList(String token, User user) {
        long expirationInSeconds = expiration;
        LocalDateTime expirationDateTime = LocalDateTime.now().plusSeconds(expirationInSeconds/1000);
        // Tạo mới một token cho người dùng
        BlacklistToken blacklistToken = BlacklistToken.builder()
                .user(user)
                .token(token)
                .tokenType("Bearer")
                .expirationDate(expirationDateTime)
                .build();
        blackListTokenRepository.save(blacklistToken);
    }

    @Override
    public boolean isBlacklisted(String token) {
        return blackListTokenRepository.findByToken(token) != null;
    }


    @Scheduled(initialDelay = 70000L, fixedDelay = 3600000L)
    public void cleanupTokens() {
        List<BlacklistToken> tokensToClean = blackListTokenRepository.findByExpirationDateBefore(LocalDateTime.now());
        System.out.println(LocalDateTime.now());
        tokensToClean.forEach(blacklistToken -> blackListTokenRepository.delete(blacklistToken));
    }
}
