package com.sivaram.ecommapi.service.inf;

import com.sivaram.ecommapi.model.dto.request.UpdateOrderStatusRequest;
import com.sivaram.ecommapi.model.dto.response.OrderResponse;

import java.util.List;

public interface IOrderService {
    OrderResponse placeOrder();

    OrderResponse getOrderById(Long id);

    List<OrderResponse> getMyOrders();

    OrderResponse updateOrderStatus(UpdateOrderStatusRequest request);
}
