package ecomers.demo.user.service;

import ecomers.demo.user.dto.*;

public interface UserService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    UserResponse getProfile(String email);
    UserResponse updateProfile(String email, UpdateProfileRequest request);
}
