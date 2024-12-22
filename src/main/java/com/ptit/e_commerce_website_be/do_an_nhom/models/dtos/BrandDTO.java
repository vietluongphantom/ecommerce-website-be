package com.ptit.e_commerce_website_be.do_an_nhom.models.dtos;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandDTO {

    @JsonProperty("id")
    private Long id;

    @NotNull(message = "Name is required")
    private String name;

    private String icon;

    private Boolean status;

    private String description;

    @JsonProperty("shop_id")
    private Long shopId;
}
