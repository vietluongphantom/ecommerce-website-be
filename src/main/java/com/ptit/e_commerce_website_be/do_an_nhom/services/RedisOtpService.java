package com.ptit.e_commerce_website_be.do_an_nhom.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.RegisterUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisOtpService {

    private static final int OTP_EXPIRATION_MINUTES = 5;
    private static final int RATE_LIMIT_MINUTES = 1;

    private final RedisTemplate<String, String> redisTemplate;
    private final PasswordEncoder passwordEncoder;

    public Integer generateAndSaveOtp(String email) {
        if (isRateLimited(email)) {
            log.warn("OTP request rate limit exceeded for email: {}", email);
            throw new RuntimeException("OTP request rate limit exceeded. Please wait before trying again.");
        }

        Random random = new Random();
        int otp = random.nextInt(900000) + 100000;
        String hashedOtp = passwordEncoder.encode(String.valueOf(otp));

        redisTemplate.opsForValue().set(email, hashedOtp, OTP_EXPIRATION_MINUTES, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(rateLimitKey(email), "1", RATE_LIMIT_MINUTES, TimeUnit.MINUTES);
        log.info("OTP generated and stored for email: {}", email);
        return otp;
    }

    public boolean verifyOtp(String email, Integer otp) {
        String hashedOtp = redisTemplate.opsForValue().get(email);

        if (hashedOtp != null && passwordEncoder.matches(String.valueOf(otp), hashedOtp)) {
            redisTemplate.delete(email);
            redisTemplate.delete(rateLimitKey(email));
            log.info("OTP verified and removed for email: {}", email);
            return true;
        } else {
            log.warn("Invalid or expired OTP attempt for email: {}", email);
            return false;
        }
    }

    private boolean isRateLimited(String email) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(rateLimitKey(email)));
    }

    private String rateLimitKey(String email) {
        return email + ":ratelimit";
    }

    // Testing
    public void storeTemporaryUser(RegisterUserDto registerUserDto) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonUserDto = objectMapper.writeValueAsString(registerUserDto);
            redisTemplate.opsForValue().set(registerUserDto.getEmail() + ":user", jsonUserDto, 5, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            log.error("Error storing temporary user: {}", e.getMessage());
        }
    }

    public RegisterUserDto retrieveTemporaryUser(String email) {
        String jsonUserDto = (String) redisTemplate.opsForValue().get(email + ":user");
        if (jsonUserDto != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.readValue(jsonUserDto, RegisterUserDto.class);
            } catch (JsonProcessingException e) {
                log.error("Error retrieving temporary user: {}", e.getMessage());
            }
        }
        return null;
    }
}

