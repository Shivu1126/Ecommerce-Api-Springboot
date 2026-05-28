package com.sivaram.ecommapi.controller;


import com.sivaram.ecommapi.model.dto.request.CartItemRequest;
import com.sivaram.ecommapi.model.dto.response.CartResponse;
import com.sivaram.ecommapi.service.inf.ICartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final ICartService cartService;

    @GetMapping("")
    public ResponseEntity<CartResponse> getCart() {
        CartResponse response = cartService.getCart();
        return ResponseEntity.ok(response);
    }

    @PostMapping("")
    public ResponseEntity<String> addToCart(@Valid @RequestBody CartItemRequest request) {
        String response = cartService.addToCart(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("")
    public ResponseEntity<CartResponse> updateCartItem(@Valid @RequestBody CartItemRequest request) {
        CartResponse response = cartService.updateCartItem(request);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<CartResponse> removeFromCart(@PathVariable Long cartItemId) {
        CartResponse response = cartService.removeFromCart(cartItemId);
        return ResponseEntity.ok(response);
    }

}
