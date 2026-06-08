package com.example.cvmanager.auth.service;

import com.example.cvmanager.auth.dto.LoginRequest;
import com.example.cvmanager.auth.dto.LoginResponse;
import com.example.cvmanager.admin.service.AdminProperties;
import com.example.cvmanager.common.exception.BadRequestException;
import com.example.cvmanager.common.security.AsIsSecurityProperties;
import com.example.cvmanager.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AsIsSecurityProperties securityProperties;
    private final AdminProperties adminProperties;

    public AuthService(
            UserRepository userRepository,
            AsIsSecurityProperties securityProperties,
            AdminProperties adminProperties) {
        this.userRepository = userRepository;
        this.securityProperties = securityProperties;
        this.adminProperties = adminProperties;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        var user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new BadRequestException("Invalid email or password", "AUTH_INVALID"));

        if (!user.getPassword().equals(request.password())) {
            throw new BadRequestException("Invalid email or password", "AUTH_INVALID");
        }

        String token = securityProperties.demoTokenPrefix() + "-" + user.getId();
        boolean admin = adminProperties.email().equalsIgnoreCase(user.getEmail());
        return new LoginResponse(user.getId(), user.getEmail(), user.getDisplayName(), admin, token);
    }
}
