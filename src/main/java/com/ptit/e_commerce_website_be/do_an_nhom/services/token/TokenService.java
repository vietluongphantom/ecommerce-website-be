package com.ptit.e_commerce_website_be.do_an_nhom.services.token;

import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Token;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import org.springframework.stereotype.Service;

@Service
public interface TokenService {
    Token addToken(User user, String token);
    Token refreshToken(String refreshToken, User user);
}
