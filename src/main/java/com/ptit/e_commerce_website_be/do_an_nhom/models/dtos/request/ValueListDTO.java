package com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.request;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.AttributeValuesDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValueListDTO {
    List<AttributeValuesDto> attributeValuesDTOS;
}
