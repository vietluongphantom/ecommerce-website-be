package com.ptit.e_commerce_website_be.do_an_nhom.models.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductItemAttributesDTO {
    //
//    @NotBlank(message = "Value cannot be blank")
    @JsonProperty("value")
    private String value;

    @Min(value=1, message =  "Product values attributes ID is required")
    @JsonProperty("attribute_value_id")
    private Long attributeValueId;

    @Min(value=1, message =  "Product attributes ID is required")
    @JsonProperty("product_attributes_id")
    private Long productAttributeId;

    private Long id;

    public ProductItemAttributesDTO(String value, Long productAttributesId) {
    }
}

