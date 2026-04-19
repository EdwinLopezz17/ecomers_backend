package ecomers.demo.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateProductRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message="The price is required")
    @DecimalMin(value = "0.01", message = "The price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "The stock is required")
    @Min(value = 0, message = "The stock cannot must be negative")
    private Integer stock;

    @NotNull(message = "The category is required")
    private Long categoryId;
}
