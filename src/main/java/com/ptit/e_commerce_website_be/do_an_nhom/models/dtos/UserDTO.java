package com.ptit.e_commerce_website_be.do_an_nhom.models.dtos;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ptit.e_commerce_website_be.do_an_nhom.models.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Size(max = 200, message = "Email cannot exceed 200 characters")
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "Full name is required")
    @Size(max = 200, message = "full name cannot exceed 200 characters")
    @JsonProperty("full_name")
    private String fullName;

    @NotBlank(message = "Password is required")
    @JsonProperty("password")
    @JsonIgnore
    private String password;

    @NotBlank(message = "Phone number is required")
    @JsonProperty("phone")
    private String phone;

    @NotNull(message = "status is required")
    @JsonProperty("status")
    private boolean status;

    @JsonProperty("gender")
    @NotBlank(message = "gender is required")
    private Gender gender;

//    public enum Gender {
//        MALE,
//        FEMALE,
//        OTHER
//    }
}

