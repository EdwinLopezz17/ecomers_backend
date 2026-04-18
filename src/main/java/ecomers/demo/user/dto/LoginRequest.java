package ecomers.demo.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "The email is required")
    private String email;

    @NotBlank(message="The password is required")
    private String password;
}
