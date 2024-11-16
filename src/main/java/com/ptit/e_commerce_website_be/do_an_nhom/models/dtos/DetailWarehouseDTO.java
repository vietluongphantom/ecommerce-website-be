package com.ptit.e_commerce_website_be.do_an_nhom.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DetailWarehouseDTO {
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Name must be between 3 and 200 characters")
    private String name;

    @JsonProperty("country")
//    @NotNull(message = "Country is required")
    @Size(max = 45, message = "Country cannot be longer than 45 characters")
    private String country;

    @JsonProperty("province")
//    @NotNull(message = "Province is required")
    @Size(max = 45, message = "Province cannot be longer than 45 characters")
    private String province;

    @JsonProperty("district")
//    @NotNull(message = "District is required")
    @Size(max = 45, message = "District cannot be longer than 45 characters")
    private String district;

    @JsonProperty("commune")
//    @NotNull(message = "Commune is required")
    @Size(max = 45, message = "Commune cannot be longer than 45 characters")
    private String commune;

    @JsonProperty("address_detail")
//    @NotNull(message = "Address detail is required")
    @Size(max = 255, message = "Address detail cannot be longer than 255 characters")
    private String addressDetail;

    public DetailWarehouseDTO(String name, String country, String province, String district, String commune, String addressDetail) {
        this.name = name;
        this.country = country;
        this.province = province;
        this.district = district;
        this.commune = commune;
        this.addressDetail = addressDetail;
    }
}

