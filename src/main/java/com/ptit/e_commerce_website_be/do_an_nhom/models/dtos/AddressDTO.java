package com.ptit.e_commerce_website_be.do_an_nhom.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddressDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("country")
    @NotNull(message = "Country is required")
    @Size(max = 45, message = "Country cannot be longer than 45 characters")
    private String country;

    @JsonProperty("province")
    @NotNull(message = "Province is required")
    @Size(max = 45, message = "Province cannot be longer than 45 characters")
    private String province;

    @JsonProperty("district")
    @NotNull(message = "District is required")
    @Size(max = 45, message = "District cannot be longer than 45 characters")
    private String district;

    @JsonProperty("commune")
    @NotNull(message = "Commune is required")
    @Size(max = 45, message = "Commune cannot be longer than 45 characters")
    private String commune;

    @JsonProperty("address_detail")
    @NotNull(message = "Address detail is required")
    @Size(max = 255, message = "Address detail cannot be longer than 255 characters")
    private String addressDetail;

    @JsonProperty("user_id")
    @NotNull(message = "User ID is required")
    private Long userId;
}
