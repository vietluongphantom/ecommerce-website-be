package com.ptit.e_commerce_website_be.do_an_nhom.repositories;


import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.DetailInventoryDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Supply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplyRepository extends JpaRepository<Supply, Long> {

    @Query(name = "Supply.getAllImport", nativeQuery = true)
    List<DetailInventoryDTO> getAllImport(@Param("warehouse") String warehouse, @Param("supplier") String supplier
            , @Param("location") String location, @Param("sku_code") String skuCode, @Param("name") String name
            , @Param("created_at")String createdAt, @Param("shop_id") Long shopId, @Param("limit") Integer limit, @Param("offSet") Long offSet);

    @Query(name = "Supply.getAllImportData", nativeQuery = true)
    List<DetailInventoryDTO> getAllImportData(@Param("shop_id") Long shopId);


    @Query(name = "Supply.getAllListImport", nativeQuery = true)
    List<DetailInventoryDTO> getAllListImport(@Param("shop_id") Long shopId);



    @Query(nativeQuery = true, value =
            "SELECT COUNT(DISTINCT s.id) " +
                    "FROM Supply s " +
                    "INNER JOIN Product_item pi ON pi.id = s.product_item_Id " +
                    "INNER JOIN Warehouse w ON w.id = s.warehouse_Id " +
                    "INNER JOIN Product p ON p.id = pi.product_Id " +
                    "WHERE s.status = 1 AND w.shop_Id = :shop_id " +
                    "AND w.name LIKE CONCAT('%', :warehouse,'%') " +
                    "AND s.supplier LIKE CONCAT('%',:supplier,'%') " +
                    "AND s.location LIKE CONCAT('%',:location,'%') " +
                    "AND pi.sku_code LIKE CONCAT('%',:sku_code,'%') " +
                    "AND p.name LIKE CONCAT('%',:name,'%') " +
                    "AND s.created_at LIKE CONCAT('%',:created_at,'%') " +
                    "ORDER BY s.created_at ")
    int countAllImport(@Param("warehouse") String warehouse, @Param("supplier") String supplier
            ,@Param("location") String location,@Param("sku_code") String skuCode,@Param("name") String name
            ,@Param("created_at")String createdAt,@Param("shop_id") Long shopId);

    @Query(nativeQuery = true, value =
            "SELECT COUNT(DISTINCT s.id) " +
                    "FROM Supply s " +
                    "INNER JOIN Product_item pi ON pi.id = s.product_item_Id " +
                    "INNER JOIN Warehouse w ON w.id = s.warehouse_Id " +
                    "INNER JOIN Product p ON p.id = pi.product_Id " +
                    "WHERE s.status = 0 AND w.shop_Id = :shop_id " +
                    "AND w.name LIKE CONCAT('%', :warehouse,'%') " +
                    "AND s.supplier LIKE CONCAT('%',:supplier,'%') " +
                    "AND s.location LIKE CONCAT('%',:location,'%') " +
                    "AND pi.sku_code LIKE CONCAT('%',:sku_code,'%') " +
                    "AND p.name LIKE CONCAT('%',:name,'%') " +
                    "AND s.created_at LIKE CONCAT('%',:created_at,'%') " +
                    "ORDER BY s.created_at ")
    int countAllExport(@Param("warehouse") String warehouse, @Param("supplier") String supplier
            ,@Param("location") String location,@Param("sku_code") String skuCode,@Param("name") String name
            ,@Param("created_at")String createdAt,@Param("shop_id") Long shopId);

    @Query(name = "Supply.getAllExport", nativeQuery = true)
    List<DetailInventoryDTO> getAllExport(@Param("warehouse") String warehouse, @Param("supplier") String supplier
            , @Param("location") String location, @Param("sku_code") String skuCode, @Param("name") String name
            , @Param("created_at")String createdAt, @Param("shop_id") Long shopId , @Param("limit") Integer limit, @Param("offSet") Long offSet);
}

