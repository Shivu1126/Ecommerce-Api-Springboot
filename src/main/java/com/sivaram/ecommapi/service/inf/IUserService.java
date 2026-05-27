package com.sivaram.ecommapi.service.inf;

import com.sivaram.ecommapi.model.User;
import com.sivaram.ecommapi.model.dto.response.UserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface IUserService extends UserDetailsService {
    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
    User getCurrentUser();
}
