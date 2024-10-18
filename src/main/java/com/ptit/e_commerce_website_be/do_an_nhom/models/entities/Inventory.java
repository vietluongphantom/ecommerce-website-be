package com.ghtk.ecommercewebsite.models.entities;


import com.ghtk.ecommercewebsite.models.dtos.DetailInventoryDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@SqlResultSetMapping(
        name = "DetailInventoryMapping",
        classes = @ConstructorResult(
                targetClass = DetailInventoryDTO.class,
                columns = {
                        @ColumnResult(name = "quantity", type = Integer.class),
                        @ColumnResult(name = "sku_code", type = String.class),
                        @ColumnResult(name = "name", type = String.class),
                        @ColumnResult(name = "warehouse", type = String.class)
                }
        )
)

@NamedNativeQuery(
        name = "Inventory.getAllInventory",
        query = "SELECT i.quantity, pi.sku_code, p.name, w.name AS warehouse " +
                "FROM Inventory i " +
                "INNER JOIN Product_item pi ON pi.id = i.product_item_Id " +
                "INNER JOIN Warehouse w ON w.id = i.warehouse_Id " +
                "INNER JOIN Product p ON p.id = pi.product_Id " +
                "WHERE w.shop_Id = :shop_id " +
                "AND w.name LIKE CONCAT('%', :warehouse,'%') " +
                "AND pi.sku_code LIKE CONCAT('%',:sku_code,'%') " +
                "AND p.name LIKE CONCAT('%',:name,'%') " +
                "LIMIT :limit OFFSET :offSet",
        resultSetMapping = "DetailInventoryMapping"
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

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

}
