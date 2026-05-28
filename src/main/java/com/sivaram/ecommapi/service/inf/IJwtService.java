package com.sivaram.ecommapi.service.inf;

import org.springframework.security.core.userdetails.UserDetails;

public interface IJwtService {
    public String generateToken(String email);
    public boolean validateToken(String token, UserDetails userDetails);
    public String extractEmail(String token);
}
