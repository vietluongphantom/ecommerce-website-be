package com.ptit.e_commerce_website_be.do_an_nhom.models.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.ptit.e_commerce_website_be.do_an_nhom.models.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

//    @NotBlank(message = "Password is required")
//    private String password;

    @JsonProperty("full_name")
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^0[0-9]{9}$", message = "Invalid phone number")
    private String phone;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "Province is required")
    private String province;

    @NotBlank(message = "District is required")
    private String district;

    @NotBlank(message = "Commune is required")
    private String commune;

    private String avatar;

    @JsonProperty("address_detail")
    @NotBlank(message = "Address detail is required")
    private String addressDetail;

}

