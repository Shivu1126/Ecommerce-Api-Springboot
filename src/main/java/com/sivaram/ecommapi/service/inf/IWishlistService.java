package com.sivaram.ecommapi.service.inf;

import com.sivaram.ecommapi.model.dto.request.WishlistItemRequest;
import com.sivaram.ecommapi.model.dto.response.WishlistResponse;

public interface IWishlistService {
    String addToWishlist(WishlistItemRequest request);
    WishlistResponse removeFromWishlist(Long wishlistItemId);
    WishlistResponse getWishlist();
}
