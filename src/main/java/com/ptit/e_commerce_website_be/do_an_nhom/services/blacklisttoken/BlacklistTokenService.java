package com.ptit.e_commerce_website_be.do_an_nhom.services.blacklisttoken;


import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import org.springframework.stereotype.Service;

@Service
public interface BlacklistTokenService {
    void addToBlackList(String token, User user);

    boolean isBlacklisted(String token);
}
