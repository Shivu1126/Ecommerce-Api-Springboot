package com.sivaram.ecommapi.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    @NotBlank(message = "Title is required")
    private String title;

    @Size(min = 1, max = 100, message = "Brand must be between 1 and 100 characters")
    @NotBlank(message = "Brand is required")
    private String brand;

    @Size(min = 1, max = 100, message = "Category must be between 1 and 100 characters")
    @NotBlank(message = "Category is required")
    private String category;

    @Min(value = 1, message = "Quantity must be at least 1")
    @NotNull(message = "Quantity is required")
    private Integer quantity;

    @Min(value = 10, message = "Price must be at least 10")
    @NotNull(message = "Price is required")
    private Double price;

    @Size(min = 1, max = 1000, message = "Description must be between 1 and 1000 characters")
    @NotBlank(message = "Description is required")
    private String description;

    @Size(min = 8, max = 255, message = "Image URL must be between 8 and 255 characters")
    @NotBlank(message = "Image URL is required")
    private String imageUrl;
}
