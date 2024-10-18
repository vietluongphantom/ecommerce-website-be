package com.ghtk.ecommercewebsite.models.entities;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ghtk.ecommercewebsite.models.dtos.DetailInventoryDTO;
import com.ghtk.ecommercewebsite.models.dtos.ProductItemAttributesDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.util.List;

@SqlResultSetMapping(
        name = "DetailProductItem",
        classes = @ConstructorResult(
                targetClass = DetailInventoryDTO.class,
                columns = {
                        @ColumnResult(name = "price", type = BigDecimal.class),
                        @ColumnResult(name = "quantity", type = Integer.class),
                        @ColumnResult(name = "name", type = String.class),
                        @ColumnResult(name = "sku_code", type = String.class),
                        @ColumnResult(name = "product_id", type = Long.class),
                        @ColumnResult(name = "list_product_item", type = ProductItemAttributesDTO.class),
                }
        )
)

@NamedNativeQuery(
        name = "ProductItem.GetAllProductItemByProductId",
        query = "SELECT s.quantity, pi.sku_code, p.name, s.supplier, pi.import_price, w.name AS warehouse, s.location " +
                "FROM Supply s " +
                "INNER JOIN Product_item pi ON pi.id = s.product_item_Id " +
                "INNER JOIN Warehouse w ON w.id = s.warehouse_Id " +
                "INNER JOIN Product p ON p.id = pi.product_Id " +
                "WHERE  s.status = 1 AND w.shop_Id = :shop_id ",
        resultSetMapping = "DetailProductItem"
)





@Entity
@Table(name = "product_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "price", precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "sale_price", precision = 12, scale = 2)
    private BigDecimal salePrice;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "sku_code")
    private String skuCode;

    @Column(name = "is_delete")
    private Boolean isDelete;

    @Column(name = "import_price", precision = 12, scale = 2)
    private  BigDecimal importPrice;

    @Column(name = "total_sold")
    private  Long totalSold;

}
