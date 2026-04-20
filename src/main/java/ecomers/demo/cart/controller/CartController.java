package ecomers.demo.cart.controller;

import ecomers.demo.cart.dto.AddItemRequest;
import ecomers.demo.cart.dto.CartResponse;
import ecomers.demo.cart.service.CartService;
import ecomers.demo.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getMyCart(
            @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(ApiResponse.ok("Cart",
                cartService.getMyCart(userDetails.getUsername())));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddItemRequest request){
        return ResponseEntity.ok(ApiResponse.ok("Product added to cart",
                cartService.addItem(userDetails.getUsername(), request)));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long itemId,
            @RequestParam Integer quantity){
        return ResponseEntity.ok(ApiResponse.ok("Cart updated",
                cartService.updateItem(userDetails.getUsername(), itemId, quantity)));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long itemId){
        return ResponseEntity.ok(ApiResponse.ok("Item deleted",
                cartService.removeItem(userDetails.getUsername(), itemId)));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @AuthenticationPrincipal UserDetails userDetails){
        cartService.clearCart(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Cart clear completed",null));
    }
}
