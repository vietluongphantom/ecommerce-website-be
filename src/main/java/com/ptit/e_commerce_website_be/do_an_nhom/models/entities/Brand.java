package com.ghtk.ecommercewebsite.models.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "brand")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime createdAt;

    @Column(name = "modified_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime modifiedAt;

    @Column(name = "description")
    private String description;

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    private boolean status;


//    @Column(name = "shop_id", nullable = false)
//    private Long shopId;

    @Column(name = "is_delete")
    private Boolean isDelete;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        modifiedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        modifiedAt = LocalDateTime.now();
    }
}
