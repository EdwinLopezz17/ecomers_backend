package ecomers.demo.cart.service.impl;

import ecomers.demo.cart.dto.AddItemRequest;
import ecomers.demo.cart.dto.CartItemResponse;
import ecomers.demo.cart.dto.CartResponse;
import ecomers.demo.cart.entity.Cart;
import ecomers.demo.cart.entity.CartItem;
import ecomers.demo.cart.repository.CartItemRepository;
import ecomers.demo.cart.repository.CartRepository;
import ecomers.demo.cart.service.CartService;
import ecomers.demo.exception.ResourceNotFoundException;
import ecomers.demo.product.entity.Product;
import ecomers.demo.product.repository.ProductRepository;
import ecomers.demo.user.entity.User;
import ecomers.demo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CartResponse getMyCart(String email) {
        User user = findUser(email);
        Cart cart = getOrCreateCart(user);
        return toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addItem(String email, AddItemRequest request) {
        User user = findUser(email);
        Cart cart = getOrCreateCart(user);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(()->new ResourceNotFoundException("Product  not found"));

        if(product.getStock() < request.getQuantity())
            throw new RuntimeException("Insufficient stock. Available: "+ product.getStock());

        cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .ifPresentOrElse(
                        item ->item.setQuantity(item.getQuantity() + request.getQuantity()),
                        ()->{
                            CartItem newItem = CartItem.builder()
                                    .cart(cart)
                                    .product(product)
                                    .quantity(request.getQuantity())
                                    .build();
                            cart.getItems().add(newItem);
                        }
                );
        return toResponse(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public CartResponse updateItem(String email, Long itemId, Integer quantity) {
        User user = findUser(email);
        Cart cart = getOrCreateCart(user);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(()->new ResourceNotFoundException("Item not found in cart"));

        if(quantity <= 0){
            cart.getItems().remove(item);
        }else{
            if(item.getProduct().getStock() < quantity)
                throw new RuntimeException("Insufficient stock. Available: "+item.getProduct().getStock());

            item.setQuantity(quantity);
        }
        return toResponse(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public CartResponse removeItem(String email, Long itemId) {
        User user = findUser(email);
        Cart cart = getOrCreateCart(user);

        cart.getItems().removeIf(i->i.getId().equals(itemId));
        return toResponse(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public void clearCart(String email){
        User user = findUser(email);
        Cart cart = getOrCreateCart(user);

        cart.getItems().clear();
        cartRepository.save(cart);
    }



    private Cart getOrCreateCart(User user){
        return cartRepository.findByUserId(user.getId())
                .orElseGet(()->cartRepository.save(
                        Cart.builder()
                                .user(user)
                                .build()
                ));
    }

    private User findUser(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(()->new ResourceNotFoundException("User not found"));
    }

    private CartResponse toResponse(Cart cart){
        List<CartItemResponse> items = cart.getItems().stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());

        BigDecimal total = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = items.stream()
                .mapToInt(CartItemResponse::getQuantity)
                .sum();

        return CartResponse.builder()
                .id(cart.getId())
                .items(items)
                .totalItems(totalItems)
                .total(total)
                .build();
    }

    private CartItemResponse toItemResponse(CartItem item){
        BigDecimal subtotal = item.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));

        String image = (item.getProduct().getImages() != null && !item.getProduct().getImages().isEmpty())
                ? item.getProduct().getImages().get(0)
                : null;

        return CartItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .productImage(image)
                .unitPrice(item.getProduct().getPrice())
                .quantity(item.getQuantity())
                .subtotal(subtotal)
                .build();
    }
}
