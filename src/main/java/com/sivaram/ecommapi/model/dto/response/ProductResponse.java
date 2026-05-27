package com.sivaram.ecommapi.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String title;
    private String brand;
    private String category;
    private String description;
    private Double price;
    private Integer stockQuantity;
    private String imageUrl;
    private Long sellerId;
    private String sellerName;
}
