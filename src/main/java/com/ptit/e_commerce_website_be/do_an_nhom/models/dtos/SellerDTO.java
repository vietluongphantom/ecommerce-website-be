package com.ptit.e_commerce_website_be.do_an_nhom.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerDTO {

    @JsonProperty("tax")
    private String tax;

    @JsonProperty("cccd")
    private String cccd;

    @NotNull(message = "Shop ID is required")
    @JsonProperty("shop_id")
    private Long shopId;

    @NotNull(message = "User ID is required")
    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

}
