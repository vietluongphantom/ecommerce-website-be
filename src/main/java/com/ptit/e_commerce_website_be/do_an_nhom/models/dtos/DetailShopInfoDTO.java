package com.ptit.e_commerce_website_be.do_an_nhom.models.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DetailShopInfoDTO {
    @NotBlank(message = "Name is required")
    @Size(max = 60, message = "Name cannot exceed 60 characters")
    @JsonProperty("name")
    private String name;

    @Email(message = "Mail should be valid")
    @Size(max = 90, message = "Mail cannot exceed 90 characters")
    @JsonProperty("mail")
    private String mail;

    @Size(max = 15, message = "Phone cannot exceed 15 characters")
    @JsonProperty("phone")
    private String phone;

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

    public DetailShopInfoDTO(String name, String mail, String phone, String country, String province, String district, String commune, String addressDetail) {
        this.name = name;
        this.mail = mail;
        this.phone = phone;
        this.country = country;
        this.province = province;
        this.district = district;
        this.commune = commune;
        this.addressDetail = addressDetail;
    }
}
