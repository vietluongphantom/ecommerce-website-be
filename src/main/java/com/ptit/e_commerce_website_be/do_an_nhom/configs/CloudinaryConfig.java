package com.ptit.e_commerce_website_be.do_an_nhom.configs;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {
    @Value("${cloudinary.upload.file.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.upload.file.api-key}")
    private String apiKey;

    @Value("${cloudinary.upload.file.api-secret}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary(){
        final Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        return new Cloudinary(config);
    }
}

