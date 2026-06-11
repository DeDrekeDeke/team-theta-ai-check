package com.example.cvmanager.auth.service;

import com.example.cvmanager.auth.dto.LoginRequest;
import com.example.cvmanager.auth.dto.LoginResponse;
import com.example.cvmanager.auth.security.JwtService;
import com.example.cvmanager.common.exception.BadRequestException;
import com.example.cvmanager.user.dto.UserCreateRequest;
import com.example.cvmanager.user.model.UserAccount;
import com.example.cvmanager.user.model.UserRole;
import com.example.cvmanager.user.repository.UserRepository;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase(Locale.ROOT);

        var user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new BadRequestException("Invalid email or password", "AUTH_INVALID"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadRequestException("Invalid email or password", "AUTH_INVALID");
        }

        String token = jwtService.createAccessToken(user);
        return toLoginResponse(user, token);
    }

    @Transactional
    public LoginResponse register(UserCreateRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        userRepository.findByEmailIgnoreCase(email).ifPresent(existing -> {
            throw new BadRequestException("User with this email already exists", "USER_EMAIL_EXISTS");
        });

        UserAccount user = new UserAccount(
                email,
                request.displayName().trim(),
                passwordEncoder.encode(request.password()),
                false);
        user.setRole(UserRole.USER);

        UserAccount savedUser = userRepository.save(user);
        String token = jwtService.createAccessToken(savedUser);
        return toLoginResponse(savedUser, token);
    }

    private LoginResponse toLoginResponse(UserAccount user, String token) {
        return new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getRole().name(),
                user.isAdmin(),
                token);
    }
}
