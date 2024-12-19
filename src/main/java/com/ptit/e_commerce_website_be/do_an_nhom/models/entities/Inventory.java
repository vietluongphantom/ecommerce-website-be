package com.ptit.e_commerce_website_be.do_an_nhom.models.entities;


import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.DetailInventoryDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SqlResultSetMapping(
        name = "DetailInventoryDataMapping",
        classes = @ConstructorResult(
                targetClass = DetailInventoryDTO.class,
                columns = {
                        @ColumnResult(name = "id", type = Long.class),
                        @ColumnResult(name = "quantity", type = Integer.class),
                        @ColumnResult(name = "sku_code", type = String.class),
                        @ColumnResult(name = "name", type = String.class),
                        @ColumnResult(name = "supplier", type = String.class),
                        @ColumnResult(name = "import_price", type = BigDecimal.class),
                        @ColumnResult(name = "created_at", type = LocalDateTime.class),
                        @ColumnResult(name = "product_id", type = Long.class),
                        @ColumnResult(name = "price", type = BigDecimal.class),
                }
        )
)

@SqlResultSetMapping(
        name = "DetailInventoryMapping",
        classes = @ConstructorResult(
                targetClass = DetailInventoryDTO.class,
                columns = {
                        @ColumnResult(name = "quantity", type = Integer.class),
                        @ColumnResult(name = "sku_code", type = String.class),
                        @ColumnResult(name = "name", type = String.class),
                        @ColumnResult(name = "supplier", type = String.class),
                }
        )
)

@NamedNativeQuery(
        name = "Inventory.getAllInventory",
        query = "SELECT i.quantity, pi.sku_code, p.name, w.name AS supplier " +
                "FROM Inventory i " +
                "INNER JOIN Product_item pi ON pi.id = i.product_item_id " +
                "INNER JOIN Supplier w ON w.id = i.supplier_id " +
                "INNER JOIN Product p ON p.id = pi.product_Id " +
                "WHERE w.shop_Id = :shop_id " +
                "AND w.name LIKE CONCAT('%', :supplier,'%') " +
                "AND pi.sku_code LIKE CONCAT('%',:sku_code,'%') " +
                "AND p.name LIKE CONCAT('%',:name,'%') " +
                "LIMIT :limit OFFSET :offSet",
        resultSetMapping = "DetailInventoryMapping"
)

@NamedNativeQuery(
        name = "Inventory.getAllListInventoryData",
        query = "SELECT i.id, i.quantity ,pi.sku_code , p.name, w.name AS supplier,p.created_at ,pi.import_price , pi.price, p.id as product_id " +
                "FROM Inventory i " +
                "INNER JOIN Product_item pi ON pi.id = i.product_item_id " +
                "INNER JOIN Supplier w ON w.id = i.supplier_id " +
                "INNER JOIN Product p ON p.id = pi.product_Id " +
                "WHERE w.shop_Id = :shop_id ",
        resultSetMapping = "DetailInventoryDataMapping"
)





@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;

    @Column(name = "product_item_id", nullable = false)
    private Long productItemId;

    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;

}
