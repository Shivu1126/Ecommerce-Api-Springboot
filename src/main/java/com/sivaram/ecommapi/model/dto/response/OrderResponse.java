package com.sivaram.ecommapi.model.dto.response;

import com.sivaram.ecommapi.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
        private Long orderId;
        private OrderStatus orderStatus;
        private Double totalAmount;
        private LocalDateTime orderDate;
        private Long userId;
        private String userEmail;
        private String userShippingAddress;
        private List<OrderItemResponse> orderItems;
}
