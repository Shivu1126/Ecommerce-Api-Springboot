package com.sivaram.ecommapi.service.impl;

import com.sivaram.ecommapi.exception.ResourceNotFoundException;
import com.sivaram.ecommapi.model.Product;
import com.sivaram.ecommapi.model.User;
import com.sivaram.ecommapi.model.Wishlist;
import com.sivaram.ecommapi.model.WishlistItem;
import com.sivaram.ecommapi.model.dto.request.WishlistItemRequest;
import com.sivaram.ecommapi.model.dto.response.ProductResponse;
import com.sivaram.ecommapi.model.dto.response.WishlistItemResponse;
import com.sivaram.ecommapi.model.dto.response.WishlistResponse;
import com.sivaram.ecommapi.repository.ProductRepository;
import com.sivaram.ecommapi.repository.WishlistItemRepository;
import com.sivaram.ecommapi.repository.WishlistRepository;
import com.sivaram.ecommapi.service.inf.IUserService;
import com.sivaram.ecommapi.service.inf.IWishlistService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements IWishlistService {

    private final IUserService userService;
    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final WishlistItemRepository wishlistItemRepository;

    @Override
    @Transactional
    public String addToWishlist(WishlistItemRequest request) {
        User user = userService.getCurrentUser();
        Wishlist wishlist = wishlistRepository.findByUserId(user.getId())
                .orElseGet(() -> createWishlistForUser(user));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + request.getProductId()));

        boolean isAlreadyInWishlist = wishlist.getItems().stream()
                .anyMatch(item -> item.getProduct().getId().equals(product.getId()));
        if (isAlreadyInWishlist) {
            throw new ResourceNotFoundException("Product is already in the wishlist");
        }
        WishlistItem wishlistItem = WishlistItem.builder()
                .wishlist(wishlist)
                .product(product)
                .build();
        wishlist.getItems().add(wishlistItem);
        wishlistRepository.save(wishlist);
        return "Product added to wishlist successfully";
    }

    @Override
    @Transactional
    public WishlistResponse removeFromWishlist(Long wishlistItemId) {
        User user = userService.getCurrentUser();

        Wishlist wishlist = wishlistRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist not found for user"));

        WishlistItem wishlistItem = wishlistItemRepository.findById(wishlistItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist item not found with wishlist item ID: " + wishlistItemId));

        wishlist.getItems().remove(wishlistItem);
        wishlistItemRepository.delete(wishlistItem);
        wishlistRepository.save(wishlist);
        return mapToWishlistResponse(wishlist);
    }

    @Override
    public WishlistResponse getWishlist() {
        User currentUser = userService.getCurrentUser();
        Wishlist wishlist = wishlistRepository.findByUserId(
                currentUser.getId()).orElseGet(() -> createWishlistForUser(currentUser)
        );
        return mapToWishlistResponse(wishlist);
    }

    private Wishlist createWishlistForUser(User currentUser) {
        Wishlist wishlist = Wishlist.builder()
                .user(currentUser)
                .build();
        return wishlistRepository.save(wishlist);
    }

    private WishlistResponse mapToWishlistResponse(Wishlist wishlist) {
        List<WishlistItemResponse> wishlistItems = wishlist.getItems().stream()
                .map(this::mapToWishlistItemResponse)
                .toList();
        return WishlistResponse.builder()
                .items(wishlistItems)
                .build();
    }

    private WishlistItemResponse mapToWishlistItemResponse(WishlistItem wishlistItem) {
        return WishlistItemResponse.builder()
                .id(wishlistItem.getId())
                .product(mapToProductResponse(wishlistItem.getProduct()))
                .build();
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
