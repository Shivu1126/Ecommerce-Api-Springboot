package com.sivaram.ecommapi.controller;

import com.sivaram.ecommapi.model.dto.request.UpdateOrderStatusRequest;
import com.sivaram.ecommapi.model.dto.response.OrderResponse;
import com.sivaram.ecommapi.service.inf.IOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private final IOrderService orderService;

    @PostMapping("")
    public ResponseEntity<OrderResponse> placeOrder() {
        OrderResponse response = orderService.placeOrder();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("")
    public ResponseEntity<List<OrderResponse>> getOrders() {
        List<OrderResponse> response = orderService.getMyOrders();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {
        OrderResponse response = orderService.getOrderById(orderId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("")
    public ResponseEntity<OrderResponse> updateOrderStatus(@Valid @RequestBody UpdateOrderStatusRequest request) {
        OrderResponse response = orderService.updateOrderStatus(request);
        return ResponseEntity.ok(response);
    }

}
