package com.ptit.e_commerce_website_be.do_an_nhom.models.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {

    @JsonProperty("id")
    private Long id;
    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Name is required")
    @Size(max = 300, message = "Name cannot exceed 300 characters")
    private String name;

    private Integer status;

    private String slug;

    private Long totalSold;

    private Integer productView;

    private Boolean isDelete;

    @NotNull(message = "Brand ID is required")
    private Long brandId;

    //    @NotNull(message = "Shop ID is required")
    private Long shopId;

    private List<Long> categoryIds;

    private List<String> images;

    private MultipartFile thumbnail;

    private String thumbnailImg ;

    @JsonProperty("images_text")
    private List<String> imagesText;

    @Column(name = "min_price")
    private BigDecimal minPrice;

}
