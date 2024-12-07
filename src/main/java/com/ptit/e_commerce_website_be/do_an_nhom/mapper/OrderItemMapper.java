package com.ptit.e_commerce_website_be.do_an_nhom.mapper;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.OrderItemDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderItemMapper {

    public static OrderItemDTO toDto(OrderItem orderItem) {
        return OrderItemDTO.builder()
                .id(orderItem.getId())
                .orderId(orderItem.getOrderId())
                .productItemId(orderItem.getProductItemId())
                .quantity(orderItem.getQuantity())
                .unitPrice(orderItem.getUnitPrice())
                .voucherId(orderItem.getVoucherId())
                .build();
    }

    public static OrderItem toEntity(OrderItemDTO orderItemDTO) {
        return OrderItem.builder()
                .id(orderItemDTO.getId())
                .orderId(orderItemDTO.getOrderId())
                .productItemId(orderItemDTO.getProductItemId())
                .quantity(orderItemDTO.getQuantity())
                .unitPrice(orderItemDTO.getUnitPrice())
                .voucherId(orderItemDTO.getVoucherId())
                .build();
    }

    public static List<OrderItemDTO> toDtoList(List<OrderItem> orderItemList) {
        return orderItemList.stream()
                .map(OrderItemMapper::toDto)
                .collect(Collectors.toList());
    }

    public static List<OrderItem> toEntityList(List<OrderItemDTO> orderItemDTOList) {
        return orderItemDTOList.stream()
                .map(OrderItemMapper::toEntity)
                .collect(Collectors.toList());
    }
}