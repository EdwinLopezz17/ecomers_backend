package ecomers.demo.cart.service;

import ecomers.demo.cart.dto.AddItemRequest;
import ecomers.demo.cart.dto.CartResponse;

public interface CartService {
    CartResponse getMyCart(String email);
    CartResponse addItem(String email, AddItemRequest request);
    CartResponse updateItem(String email, Long itemId, Integer quantity);
    CartResponse removeItem(String email, Long itemId);
    void clearCart(String email);
}
