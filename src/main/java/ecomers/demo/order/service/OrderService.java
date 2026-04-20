package ecomers.demo.order.service;

import ecomers.demo.order.dto.CheckOutRequest;
import ecomers.demo.order.dto.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse checkout(String email, CheckOutRequest request);
    Page<OrderResponse> getMyOrders(String email, Pageable pageable);
    OrderResponse getById(String email, Long orderId);
    OrderResponse cancelOrder(String email, Long orderId);
}
