package com.ptit.e_commerce_website_be.do_an_nhom.models.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdersDTO {

    private Long id;

    private String note;

    @NotNull(message = "Status is required")
    private OrderStatus status;

    @DecimalMin(value = "0.0", inclusive = false, message = "Total price must be positive")
    @JsonProperty("total_price")
    private BigDecimal totalPrice;

    private boolean method;

    private Long userId;

    private Long shopId;
    private Long addressID;

    @JsonProperty("address")
    private String address;

    @JsonProperty("address_detail")
    private String addressDetail;

    @JsonProperty("receiver_phone")
    private String receiverPhone;

    @JsonProperty("buyer")
    private String buyer;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonProperty("modified_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedAt;

    public enum OrderStatus {
        PENDING,        // Đang chờ xử lý
        CONFIRMED,      // Đã xác nhận
        PACKED,         // Đã đóng gói
        SHIPPED,        // Đang giao hàng
        DELIVERED,      // Đã giao hàng
        CANCELLED,      // Đã hủy
        RETURNED,       // Đã trả hàng
        COMPLETED
    }
}
