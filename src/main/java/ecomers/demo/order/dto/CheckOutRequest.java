package ecomers.demo.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckOutRequest {

    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;

}
