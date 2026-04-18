package ecomers.demo.user.service.impl;

import ecomers.demo.exception.ResourceNotFoundException;
import ecomers.demo.security.JwtUtil;
import ecomers.demo.user.dto.*;
import ecomers.demo.user.entity.User;
import ecomers.demo.user.repository.UserRepository;
import ecomers.demo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    @Override
    public AuthResponse register(RegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("The email was registered");
        if(userRepository.existsByUsername(request.getUsername()))
            throw new RuntimeException("The username in use");

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .lastName(request.getLastName())
                .build();

        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail());
        return AuthResponse.builder()
                .token(token)
                .user(toResponse(user))
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new RuntimeException("Invalid Credentials"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new RuntimeException("Invalid Credentials");

        String token = jwtUtil.generateToken(user.getEmail());
        return AuthResponse.builder()
                .token(token)
                .user(toResponse(user))
                .build();
    }

    @Override
    public UserResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return toResponse(user);
    }

    @Override
    public UserResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new ResourceNotFoundException("User not found"));

        if(request.getName() != null) user.setName(request.getName());
        if(request.getLastName() != null) user.setLastName(request.getLastName());
        if(request.getUsername() != null) user.setUsername(request.getUsername());
        if(request.getProfilePicture() != null) user.setProfilePicture(request.getProfilePicture());

        userRepository.save(user);
        return toResponse(user);
    }

    private UserResponse toResponse(User user){
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
                .lastName(user.getLastName())
                .profilePicture(user.getProfilePicture())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdateAt())
                .build();
    }
}
