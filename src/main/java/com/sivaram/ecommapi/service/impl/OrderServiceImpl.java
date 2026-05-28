package com.sivaram.ecommapi.service.impl;

import com.sivaram.ecommapi.exception.ResourceNotFoundException;
import com.sivaram.ecommapi.model.*;
import com.sivaram.ecommapi.model.dto.request.UpdateOrderStatusRequest;
import com.sivaram.ecommapi.model.dto.response.OrderItemResponse;
import com.sivaram.ecommapi.model.dto.response.OrderResponse;
import com.sivaram.ecommapi.model.enums.OrderStatus;
import com.sivaram.ecommapi.repository.CartRepository;
import com.sivaram.ecommapi.repository.OrderRepository;
import com.sivaram.ecommapi.repository.ProductRepository;
import com.sivaram.ecommapi.service.inf.IOrderService;
import com.sivaram.ecommapi.service.inf.IUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final IUserService userService;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public OrderResponse placeOrder() {
        User currentUser = userService.getCurrentUser();
        Cart cart = cartRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));
        if(cart.getItems().isEmpty()) {
            throw new ResourceNotFoundException("Cart is empty. Cannot place order.");
        }
        Order order = Order.builder()
                .user(currentUser)
                .shippingAddress(currentUser.getAddress())
                .totalAmount(0.0)
                .build();

        for(CartItem cartItem : cart.getItems()){
            Product product = cartItem.getProduct();
            if(product.getQuantity() < cartItem.getQuantity()) {
                throw new ResourceNotFoundException("Insufficient stock for product: " + product.getTitle());
            }
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .priceAtPurchase(product.getPrice())
                    .build();
            order.getItems().add(orderItem);
            order.setTotalAmount(order.getTotalAmount() + (product.getPrice() * cartItem.getQuantity()));
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }
        Order savedOrder =  orderRepository.save(order);
        cart.getItems().clear();
        cartRepository.save(cart);

        return mapToOrderResponse(savedOrder);
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        return mapToOrderResponse(orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found")));
    }

    @Override
    public List<OrderResponse> getMyOrders() {
        User currentUser = userService.getCurrentUser();
        return orderRepository.findByUserId(currentUser.getId()).stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + request.getId()));
        if(order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot update status of an order that is already delivered or cancelled.");
        }
        order.setStatus(request.getStatus());
        if(request.getStatus()==OrderStatus.CANCELLED){
            for(OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                product.setQuantity(product.getQuantity() + item.getQuantity());
                productRepository.save(product);
            }
        }
        Order updatedOrder = orderRepository.save(order);
        return mapToOrderResponse(updatedOrder);
    }

    private OrderResponse mapToOrderResponse(Order order) {

        return OrderResponse.builder()
                .orderDate(order.getOrderDate())
                .orderStatus(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .orderId(order.getId())
                .userEmail(order.getUser().getEmail())
                .userId(order.getUser().getId())
                .userShippingAddress(order.getUser().getAddress())
                .orderItems(order.getItems().stream()
                        .map(this::mapToOrderItemResponse)
                        .toList())
                .build();
    }

    private OrderItemResponse mapToOrderItemResponse(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .productId(orderItem.getProduct().getId())
                .productTitle(orderItem.getProduct().getTitle())
                .productBrand(orderItem.getProduct().getBrand())
                .productCategory(orderItem.getProduct().getCategory())
                .productImageUrl(orderItem.getProduct().getImageUrl())
                .quantity(orderItem.getQuantity())
                .priceAtPurchase(orderItem.getPriceAtPurchase())
                .subTotal(orderItem.getPriceAtPurchase() * orderItem.getQuantity())
                .build();
    }
}
