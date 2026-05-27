package com.sivaram.ecommapi.service.impl;

import com.sivaram.ecommapi.exception.BadRequestException;
import com.sivaram.ecommapi.model.Cart;
import com.sivaram.ecommapi.model.User;
import com.sivaram.ecommapi.model.Wishlist;
import com.sivaram.ecommapi.model.dto.request.LoginRequest;
import com.sivaram.ecommapi.model.dto.request.RegisterRequest;
import com.sivaram.ecommapi.model.dto.response.AuthResponse;
import com.sivaram.ecommapi.model.enums.UserRole;
import com.sivaram.ecommapi.repository.CartRepository;
import com.sivaram.ecommapi.repository.UserRepository;
import com.sivaram.ecommapi.repository.WishlistRepository;
import com.sivaram.ecommapi.service.inf.IAuthService;
import com.sivaram.ecommapi.service.inf.IJwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final WishlistRepository wishlistRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
    private final AuthenticationManager authenticationManager;
    private final IJwtService jwtService;

    @Override
    @Transactional
    public String registerUser(RegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail()))
            throw new BadRequestException("Email already in use");
        User newUser = User.builder()
                .name(request.getName())
                .username(request.getUserName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .address(request.getAddress())
                .phoneNumber(request.getPhone())
                .role(request.getRole())
                .build();
        User user = userRepository.save(newUser);
        if(request.getRole().equals(UserRole.USER)) {
            Cart cart = Cart.builder()
                    .user(user)
                    .build();
            cartRepository.save(cart);
            Wishlist wishlist = Wishlist.builder()
                    .user(user)
                    .build();
            wishlistRepository.save(wishlist);
        }
        return "User registered successfully with ID: " + user.getId();
    }

    @Override
    @Transactional
    public AuthResponse loginUser(LoginRequest request) {
//        User user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new BadRequestException("Invalid email or password"));
//        if(!user.getPassword().equals(request.getPassword()))
//            throw new BadRequestException("Invalid email or password");
//        return AuthResponse.builder()
//                .token("dummy")
//                .build();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        if(authentication.isAuthenticated()) {
            return AuthResponse.builder()
                .token(jwtService.generateToken(request.getEmail()))
                .build();
        }
        return AuthResponse.builder()
                .token("Failed")
                .build();
    }

    @Override
    public void logoutUser(String token) {

    }
}
