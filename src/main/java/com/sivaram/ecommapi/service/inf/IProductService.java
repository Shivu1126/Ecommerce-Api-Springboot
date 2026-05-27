package com.sivaram.ecommapi.service.inf;

import com.sivaram.ecommapi.model.dto.request.ProductRequest;
import com.sivaram.ecommapi.model.dto.response.ProductResponse;

import java.util.List;

public interface IProductService {
    //seller
    ProductResponse addProduct(ProductRequest request);

    ProductResponse updateProduct(ProductRequest request, Long id);

    String deleteProduct(Long id);

    //user
    List<ProductResponse> getAllProducts();

    ProductResponse getProductById(Long id);

    List<ProductResponse> getProductsByCategory(String category);

    List<ProductResponse> getProductsByPriceRange(Double minPrice, Double maxPrice);

    List<ProductResponse> searchProducts(String keyword);
}
