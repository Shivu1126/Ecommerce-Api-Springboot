package com.sivaram.ecommapi.model.dto.request;

import com.sivaram.ecommapi.model.enums.OrderStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequest {

    @NotNull(message = "Order ID cannot be null")
    @Min(value=1, message = "Order ID must be a positive number")
    private Long id;

    @NotNull(message = "Order status cannot be null")
    private OrderStatus status;
}
