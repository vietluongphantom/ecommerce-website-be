package com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttributeValuesDto {
    private Long id;
    private Long attributeId;
    private String value;
    private Boolean isDelete;
}
