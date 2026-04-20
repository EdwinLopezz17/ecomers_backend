package ecomers.demo.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddItemRequest {

    @NotNull(message = "The product is required")
    private Long productId;

    @NotNull
    @Min(value=1, message = "The minimum quantity is 1")
    private Integer quantity;
}
