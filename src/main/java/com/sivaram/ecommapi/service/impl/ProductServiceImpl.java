package com.sivaram.ecommapi.service.impl;

import com.sivaram.ecommapi.exception.ResourceNotFoundException;
import com.sivaram.ecommapi.exception.UnauthorizedException;
import com.sivaram.ecommapi.model.Product;
import com.sivaram.ecommapi.model.User;
import com.sivaram.ecommapi.model.dto.request.ProductRequest;
import com.sivaram.ecommapi.model.dto.response.ProductResponse;
import com.sivaram.ecommapi.model.enums.UserRole;
import com.sivaram.ecommapi.repository.ProductRepository;
import com.sivaram.ecommapi.service.inf.IProductService;
import com.sivaram.ecommapi.service.inf.IUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;
    private final IUserService userService;

    @Override
    @Transactional
    public ProductResponse addProduct(ProductRequest request) {
        User currentUser = userService.getCurrentUser();
        if(currentUser.getRole() != UserRole.SELLER) {
            throw new UnauthorizedException("Only sellers can add products");
        }
        Product product = Product.builder()
                .title(request.getTitle())
                .category(request.getCategory())
                .brand(request.getBrand())
                .description(request.getDescription())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .imageUrl(request.getImageUrl())
                .seller(currentUser)
                .build();
        Product savedProduct = productRepository.save(product);
        return mapToProductResponse(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(ProductRequest request, Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        User currentUser = userService.getCurrentUser();
        if(!product.getSeller().getId().equals(currentUser.getId()) && currentUser.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("Seller or Admin can only update this product");
        }
        product.setTitle(request.getTitle());
        product.setCategory(request.getCategory());
        product.setBrand(request.getBrand());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setImageUrl(request.getImageUrl());
        Product updatedProduct = productRepository.save(product);
        return mapToProductResponse(updatedProduct);
    }

    @Override
    @Transactional
    public String deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        User currentUser = userService.getCurrentUser();
        if(!product.getSeller().getId().equals(currentUser.getId()) && currentUser.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("Seller or Admin can only delete this product");
        }
        productRepository.delete(product);
        return "Product deleted successfully";
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    @Override
    public ProductResponse getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::mapToProductResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Override
    public List<ProductResponse> getProductsByCategory(String category) {
        return productRepository.findByCategory(category).stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    @Override
    public List<ProductResponse> getProductsByPriceRange(Double minPrice, Double maxPrice) {
        return productRepository.findByPriceRange(minPrice, maxPrice).stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    @Override
    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository
                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword).stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .title(product.getTitle())
                .brand(product.getBrand())
                .category(product.getCategory())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getQuantity())
                .imageUrl(product.getImageUrl())
                .sellerId(product.getSeller().getId())
                .sellerName(product.getSeller().getName())
                .build();
    }
}
