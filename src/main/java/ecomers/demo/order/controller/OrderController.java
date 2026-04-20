package ecomers.demo.order.controller;

import ecomers.demo.common.ApiResponse;
import ecomers.demo.order.dto.CheckOutRequest;
import ecomers.demo.order.dto.OrderResponse;
import ecomers.demo.order.entity.Order;
import ecomers.demo.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CheckOutRequest request){
        return ResponseEntity.ok(ApiResponse.ok("Purchase made successfully",
                orderService.checkout(userDetails.getUsername(), request)));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getMyOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size=12)Pageable pageable){
        return ResponseEntity.ok(ApiResponse.ok("My orders",
                orderService.getMyOrders(userDetails.getUsername(), pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id){
        return ResponseEntity.ok(ApiResponse.ok("Order",
                orderService.getById(userDetails.getUsername(), id)));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancel(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id){
        return ResponseEntity.ok(ApiResponse.ok("Order Canceled",
                orderService.cancelOrder(userDetails.getUsername(), id)));
    }
}
