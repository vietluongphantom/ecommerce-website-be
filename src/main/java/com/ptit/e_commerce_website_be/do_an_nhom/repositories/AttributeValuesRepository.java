package com.ptit.e_commerce_website_be.do_an_nhom.repositories;


import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.AttributeValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttributeValuesRepository extends JpaRepository<AttributeValues, Long> {

    @Modifying
    @Query("UPDATE AttributeValues av SET av.isDelete = true WHERE av.id = :id")
    void softDeleteById(Long id);

    @Query("SELECT av FROM AttributeValues av WHERE av.attributeId = :id AND av.isDelete = false")
    List<AttributeValues> findAttributeValuesByAttributeId(Long id);

    @Query("SELECT av.attributeId FROM AttributeValues av WHERE av.id = :id")
    Long findAttributeIdByAttributeValueId(Long id);
//    @Query("SELECT av FROM AttributeValues av WHERE av.attri")
//    List<AttributeValues> findAllByAttributeId(Long id);
}
