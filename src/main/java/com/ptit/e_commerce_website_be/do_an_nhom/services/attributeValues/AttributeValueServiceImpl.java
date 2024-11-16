package com.ptit.e_commerce_website_be.do_an_nhom.services.attributeValues;


import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.DataNotFoundException;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.AttributeValuesDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.AttributeValues;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.AttributeValuesRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttributeValueServiceImpl implements AttributeValueService{
    private  final AttributeValuesRepository attributeValuesRepository;

    @Override
    @Transactional
    public void createAttributeValues(AttributeValuesDTO attributeValuesDTO, Long userId){
        AttributeValues attributeValues = AttributeValues.builder()
                .attributeId(attributeValuesDTO.getAttributeId())
                .value(attributeValuesDTO.getValue())
                .isDelete(Boolean.FALSE)
                .build();
        attributeValuesRepository.save(attributeValues);
    }

    @Override
    @Transactional
    public void deleteAttributeValues(Long id, Long userid){
        attributeValuesRepository.findById(id)
                .orElseThrow(()-> new DataNotFoundException("Cannot find attribute values by id"));
        attributeValuesRepository.softDeleteById(id);
    }

    @Override
    public List<AttributeValues> getALLAttributeValues(Long id, Long userId) {
        List<AttributeValues> attributeValuesList = attributeValuesRepository.findAttributeValuesByAttributeId(id);
        return attributeValuesList;
    }
}

