package ecomers.demo.order.dto;

import ecomers.demo.order.entity.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private List<OrderItemResponse> items;
    private OrderStatus status;
    private BigDecimal total;
    private String shippingAddress;
    private LocalDateTime createdAt;
}
