package ecomers.demo.order.service.impl;

import ecomers.demo.cart.entity.Cart;
import ecomers.demo.cart.repository.CartRepository;
import ecomers.demo.exception.ResourceNotFoundException;
import ecomers.demo.order.dto.CheckOutRequest;
import ecomers.demo.order.dto.OrderItemResponse;
import ecomers.demo.order.dto.OrderResponse;
import ecomers.demo.order.entity.Order;
import ecomers.demo.order.entity.OrderItem;
import ecomers.demo.order.entity.OrderStatus;
import ecomers.demo.order.repository.OrderRepository;
import ecomers.demo.order.service.OrderService;
import ecomers.demo.product.entity.Product;
import ecomers.demo.product.repository.ProductRepository;
import ecomers.demo.user.entity.User;
import ecomers.demo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OrderResponse checkout(String email, CheckOutRequest request) {
        User buyer = findUser(email);

        Cart cart = cartRepository.findByUserId(buyer.getId())
                .orElseThrow(()->new RuntimeException("Cart is empty"));

        if(cart.getItems().isEmpty())
            throw new RuntimeException("Cart is empty");

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem->{
                    Product product = cartItem.getProduct();

                    if(product.getStock() < cartItem.getQuantity())
                        throw new RuntimeException("Insufficient stock for: " + product.getName());

                    product.setStock(product.getStock()- cartItem.getQuantity());
                    productRepository.save(product);

                    BigDecimal subtotal = product.getPrice()
                            .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

                    return OrderItem.builder()
                            .product(product)
                            .quantity(cartItem.getQuantity())
                            .unitPrice(product.getPrice())
                            .subtotal(subtotal)
                            .build();
                }).collect(Collectors.toList());

        BigDecimal total = orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .buyer(buyer)
                .items(orderItems)
                .total(total)
                .shippingAddress(request.getShippingAddress())
                .status(OrderStatus.PAID)
                .build();

        orderItems.forEach(item->item.setOrder(order));

        Order saved = orderRepository.save(order);

        cart.getItems().clear();
        cartRepository.save(cart);

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getMyOrders(String email, Pageable pageable) {
        User buyer = findUser(email);
        return orderRepository.findByBuyerIdOrderByCreatedAtDesc(buyer.getId(), pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getById(String email, Long orderId) {
        User buyer = findUser(email);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new ResourceNotFoundException("Order not found"));

        if(!order.getBuyer().getId().equals(buyer.getId()))
            throw new RuntimeException("You do not access this order");

        return toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(String email, Long orderId) {
        User buyer = findUser(email);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new ResourceNotFoundException("Order not found"));

        if(!order.getBuyer().getId().equals(buyer.getId()))
            throw new RuntimeException("You do not access this order");

        if(order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED)
            throw  new RuntimeException("An order that has already been shipped or delivered cannot be cancelled");

        order.getItems().forEach(item->{
            Product product = item.getProduct();
            product.setStock(product.getStock()+ item.getQuantity());
            productRepository.save(product);
        });

        order.setStatus(OrderStatus.CANCELLED);
        return toResponse(orderRepository.save(order));
    }



    private User findUser(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(()->new ResourceNotFoundException("User nor found"));
    }

    private OrderResponse toResponse(Order order){
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item->OrderItemResponse.builder()
                        .productId(item.getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subtotal(item.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .items(items)
                .status(order.getStatus())
                .total(order.getTotal())
                .shippingAddress(order.getShippingAddress())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
