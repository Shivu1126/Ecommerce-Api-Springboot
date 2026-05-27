package com.sivaram.ecommapi.service.inf;

import com.sivaram.ecommapi.model.dto.request.CartItemRequest;
import com.sivaram.ecommapi.model.dto.response.CartResponse;

public interface ICartService {
    String addToCart(CartItemRequest request);
    CartResponse removeFromCart(Long cartItemId);
    CartResponse updateCartItem(CartItemRequest request);
    CartResponse getCart();
}
