package com.ptit.e_commerce_website_be.do_an_nhom.repositories;


import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.DetailWarehouseDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Warehouse;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    @Query(name = "Warehouse.getDetailWarehouseInfo", nativeQuery = true)
    DetailWarehouseDTO findDetailByWarehouseId(@Param("warehouseId") Long warehouseId);

    @Query("SELECT w.shopId FROM Warehouse w WHERE w.id = :id")
    Long findShopIdById(@Param("id") Long id);

    @Query(value = "SELECT * FROM warehouse w " +
            "WHERE w.shop_id = ?1 " +
            "AND w.name LIKE CONCAT('%',?2,'%') " +
            "AND w.is_delete = 0", nativeQuery = true)
    Page<Warehouse> findByShopId(Long id, String name, Pageable pageable) ;


    @Query("SELECT COUNT(*) FROM Warehouse w WHERE w.shopId = ?1")
    Long getQuantityByShopId(Long shopId);
}

