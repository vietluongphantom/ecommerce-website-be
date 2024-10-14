package com.ptit.e_commerce_website_be.do_an_nhom.models.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {

    @JsonProperty("message")
    private String message;

    private String token;

    @JsonProperty("expires_in")
    private long expiresIn;

    @JsonProperty("refresh_token")
    private String refreshToken;

    private String tokenType = "Bearer";

//    private List<String> roles;
}
