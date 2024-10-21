package com.ptit.e_commerce_website_be.do_an_nhom.models.dtos;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshTokenDTO {
    @NotBlank
    private String refreshToken;
}
