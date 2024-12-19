package com.ptit.e_commerce_website_be.do_an_nhom.repositories;


import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.DetailSupplierDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Supplier;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    @Query(name = "Supplier.getDetailSupplierInfo", nativeQuery = true)
    DetailSupplierDTO findDetailBySupplierId(@Param("supplierId") Long supplierId);

    @Query("SELECT w.shopId FROM Supplier w WHERE w.id = :id")
    Long findShopIdById(@Param("id") Long id);

    @Query(value = "SELECT * FROM supplier w " +
            "WHERE w.shop_id = ?1 " +
            "AND w.name LIKE CONCAT('%',?2,'%') " +
            "AND w.is_delete = 0", nativeQuery = true)
    Page<Supplier> findByShopId(Long id, String name, Pageable pageable) ;


    @Query("SELECT COUNT(*) FROM Supplier w WHERE w.shopId = ?1")
    Long getQuantityByShopId(Long shopId);
}

