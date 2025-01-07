package com.ptit.e_commerce_website_be.do_an_nhom.services.attributeValues;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.AttributeValuesDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.request.AttributeValuesDto;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.AttributeValues;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AttributeValueService {
    void createAttributeValues(AttributeValuesDTO attributeValuesDTO, Long userId);
    void deleteAttributeValues(Long id, Long userid);
    List<AttributeValues> getALLAttributeValues(Long id, Long userId);

    List<AttributeValues> updateALLAttributeValues(List<AttributeValuesDto> attributeValuesDTOS);

}

