package ecomers.demo.user.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String name;
    private String lastName;
    private String username;
    private String profilePicture;
}
