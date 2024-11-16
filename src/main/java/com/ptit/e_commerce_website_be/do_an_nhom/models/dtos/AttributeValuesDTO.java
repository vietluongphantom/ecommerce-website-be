package com.ptit.e_commerce_website_be.do_an_nhom.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttributeValuesDTO {
    @JsonProperty("attribute_id")
    private Long attributeId;

    private String value;
}

