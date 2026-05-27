package com.sivaram.ecommapi.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemResponse {
    private Long id;
    private Long productId;
    private String productTitle;
    private String productBrand;
    private String productCategory;
    private String productImageUrl;
    private Integer quantity;
    private Double priceAtPurchase;
    private Double subTotal;
}
