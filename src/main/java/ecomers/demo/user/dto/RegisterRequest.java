package ecomers.demo.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "The username is required")
    @Size(min=3, max=30, message="The user name need 3 and 30 characters")
    private String username;

    @NotBlank(message = "The email is required")
    @Email(message="Invalid Email")
    private String email;

    @NotBlank(message="Password is required")
    @Size(min=5, message = "The password min length is 5")
    private String password;

    private String name;

    private String lastName;
}
