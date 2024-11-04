package com.ghtk.ecommercewebsite.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ShopDTO {

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

    @NotNull(message = "Address ID is required")
    @JsonProperty("address_id")
    private Long addressId;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
