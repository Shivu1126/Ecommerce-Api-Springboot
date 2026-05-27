package com.sivaram.ecommapi.controller;

import com.sivaram.ecommapi.model.dto.request.WishlistItemRequest;
import com.sivaram.ecommapi.model.dto.response.WishlistResponse;
import com.sivaram.ecommapi.service.inf.IWishlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final IWishlistService wishlistService;

    @GetMapping("")
    public ResponseEntity<WishlistResponse> getWishlist() {
        WishlistResponse response = wishlistService.getWishlist();
        return ResponseEntity.ok(response);
    }

    @PostMapping("")
    public ResponseEntity<String> addToWishlist(@Valid @RequestBody WishlistItemRequest request) {
        String response = wishlistService.addToWishlist(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{wishlistItemId}")
    public ResponseEntity<WishlistResponse> removeFromWishlist(@PathVariable Long wishlistItemId){
        WishlistResponse response = wishlistService.removeFromWishlist(wishlistItemId);
        return ResponseEntity.ok(response);
    }

}
