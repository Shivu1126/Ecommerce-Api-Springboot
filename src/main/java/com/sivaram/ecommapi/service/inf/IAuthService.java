package com.sivaram.ecommapi.service.inf;

import com.sivaram.ecommapi.model.dto.request.LoginRequest;
import com.sivaram.ecommapi.model.dto.request.RegisterRequest;
import com.sivaram.ecommapi.model.dto.response.AuthResponse;

public interface IAuthService {
        String registerUser(RegisterRequest request);
        AuthResponse loginUser(LoginRequest request);
        void logoutUser(String token);
}
