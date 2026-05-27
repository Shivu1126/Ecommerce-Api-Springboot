package com.sivaram.ecommapi.service.impl;

import com.sivaram.ecommapi.exception.BadRequestException;
import com.sivaram.ecommapi.exception.ResourceNotFoundException;
import com.sivaram.ecommapi.model.Cart;
import com.sivaram.ecommapi.model.CartItem;
import com.sivaram.ecommapi.model.Product;
import com.sivaram.ecommapi.model.User;
import com.sivaram.ecommapi.model.dto.request.CartItemRequest;
import com.sivaram.ecommapi.model.dto.response.CartItemResponse;
import com.sivaram.ecommapi.model.dto.response.CartResponse;
import com.sivaram.ecommapi.repository.CartItemRepository;
import com.sivaram.ecommapi.repository.CartRepository;
import com.sivaram.ecommapi.repository.ProductRepository;
import com.sivaram.ecommapi.service.inf.ICartService;
import com.sivaram.ecommapi.service.inf.IUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements ICartService {

    private final CartRepository cartRepository;
    private final IUserService userService;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional
    public String addToCart(CartItemRequest request) {
        User currentUser = userService.getCurrentUser();
        Cart cart = cartRepository.findByUserId(currentUser.getId()).orElseGet(() -> createCartForUser(currentUser));
        Product product = productRepository.findById(request.getProductId()).orElseThrow(
                () -> new ResourceNotFoundException("Product not found with ID: " + request.getProductId()));
        if(product.getQuantity() < request.getQuantity()) {
            throw new BadRequestException("Requested quantity exceeds available stock for product ID: " + request.getProductId());
        }
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()).orElse(null);
        if(cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
            cartItem.setTotalPrice(cartItem.getTotalPrice() + (product.getPrice() * request.getQuantity()));
        } else {
            cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .totalPrice(product.getPrice() * request.getQuantity())
                    .build();
            cart.addItem(cartItem);
        }
        cartItemRepository.save(cartItem);
        return "Product added to cart successfully";
    }

    @Override
    @Transactional
    public CartResponse removeFromCart(Long cartItemId) {

        User currentUser = userService.getCurrentUser();

        Cart cart = cartRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user ID: " + currentUser.getId()));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with cart item ID: " + cartItemId));

        cart.removeItem(cartItem);
        cartItemRepository.delete(cartItem);
        cartRepository.save(cart);

        return mapToCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse updateCartItem(CartItemRequest request) {
        User currentUser = userService.getCurrentUser();
        Cart cart = cartRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found "));
        if(request.getQuantity() <= 0) {
            cart.removeItem(cartItem);
            cartItemRepository.delete(cartItem);
        } else {
            if(cartItem.getProduct().getQuantity() < request.getQuantity()) {
                throw new BadRequestException("Requested quantity exceeds available stock for product ID: " + request.getProductId());
            }
            cartItem.setQuantity(request.getQuantity());
            cartItem.setTotalPrice(cartItem.getProduct().getPrice() * request.getQuantity());
        }
        cartRepository.save(cart);
        return mapToCartResponse(cart);
    }

    @Override
    public CartResponse getCart() {
        User currentUser = userService.getCurrentUser();
        Cart cart = cartRepository.findByUserId(currentUser.getId()).orElseGet(() -> createCartForUser(currentUser));
        return mapToCartResponse(cart);
    }

    private Cart createCartForUser(User currentUser) {
        Cart cart = Cart.builder()
                .user(currentUser)
                .build();
        return cartRepository.save(cart);
    }

    private CartResponse mapToCartResponse(Cart cart) {

        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::mapToCartItemResponse)
                .toList();
        Double totalPrice = 0.0;

        for(CartItemResponse itemResponse : itemResponses) {
            totalPrice += itemResponse.getSubTotal();
        }

        return CartResponse.builder()
                .id(cart.getId())
                .items(itemResponses)
                .totalPrice(totalPrice)
                .build();
    }

    private CartItemResponse mapToCartItemResponse(CartItem cartItem) {
        return CartItemResponse.builder()
                .id(cartItem.getId())
                .productId(cartItem.getProduct().getId())
                .productTitle(cartItem.getProduct().getTitle())
                .productBrand(cartItem.getProduct().getBrand())
                .productCategory(cartItem.getProduct().getCategory())
                .productImageUrl(cartItem.getProduct().getImageUrl())
                .quantity(cartItem.getQuantity())
                .productPrice(cartItem.getProduct().getPrice())
                .subTotal(cartItem.getProduct().getPrice() * cartItem.getQuantity())
                .build();
    }
}
