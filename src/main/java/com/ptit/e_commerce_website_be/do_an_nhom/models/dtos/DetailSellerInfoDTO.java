package com.ptit.e_commerce_website_be.do_an_nhom.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ptit.e_commerce_website_be.do_an_nhom.models.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@NoArgsConstructor
@Setter
@Getter
//@Builder
public class DetailSellerInfoDTO {
    @JsonProperty("tax")
    private String tax;

    @JsonProperty("cccd")
    private String cccd;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Size(max = 200, message = "Email cannot exceed 200 characters")
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "Full name is required")
    @Size(max = 200, message = "full name cannot exceed 200 characters")
    @JsonProperty("full_name")
    private String fullName;

//    @NotBlank(message = "Phone number is required")
    @JsonProperty("phone")
    private String phone;

    @JsonProperty("gender")
//    @NotBlank(message = "gender is required")
    private Gender gender;

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

    public DetailSellerInfoDTO(String tax, String cccd, String email, String gender, String fullName, String phone,
                               String country, String province, String district,
                               String commune, String addressDetail) {
        this.tax = tax;
        this.cccd = cccd;
        this.email = email;
        this.gender = (gender != null) ? Gender.valueOf(gender) : null;
        this.fullName = fullName;
        this.phone = phone;
        this.country = country;
        this.province = province;
        this.district = district;
        this.commune = commune;
        this.addressDetail = addressDetail;
    }

}
