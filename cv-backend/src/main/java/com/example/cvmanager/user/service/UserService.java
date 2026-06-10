package com.example.cvmanager.user.service;

import com.example.cvmanager.common.exception.BadRequestException;
import com.example.cvmanager.common.exception.NotFoundException;
import com.example.cvmanager.user.dto.UserCreateRequest;
import com.example.cvmanager.user.dto.UserResponse;
import com.example.cvmanager.user.dto.UserUpdateRequest;
import com.example.cvmanager.user.model.UserAccount;
import com.example.cvmanager.user.repository.UserRepository;
import java.util.List;
import java.util.Locale;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Loads all users ordered by id.
     *
     * @return user summaries without password hashes
     */
    @Transactional(readOnly = true)
    public List<UserResponse> listUsers() {
        return userRepository.findAll(Sort.by("id")).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Creates a regular user account after normalizing the email and hashing the submitted password.
     *
     * @param request validated email, display name, and raw password for the new user
     * @return created user summary without password data
     */
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        String email = normalizeEmail(request.email());
        ensureEmailAvailable(email, null);

        UserAccount user = new UserAccount(
                email,
                request.displayName().trim(),
                passwordEncoder.encode(request.password()),
                false);

        return toResponse(userRepository.save(user));
    }

    /**
     * Loads one user by database id.
     *
     * @param id user id to look up
     * @return user summary without password data
     */
    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        return userRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("User not found", "USER_NOT_FOUND"));
    }

    /**
     * Updates editable fields on an existing user account and hashes a new password when one is provided.
     *
     * @param id user id to update
     * @param request validated email, display name, optional raw password, and admin-role state to store
     * @return updated user summary without password data
     */
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        UserAccount user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found", "USER_NOT_FOUND"));

        String email = normalizeEmail(request.email());
        ensureEmailAvailable(email, id);
        ensureAnAdminRemains(user, request.admin());

        user.setEmail(email);
        user.setDisplayName(request.displayName().trim());
        if (request.password() != null) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
        user.setAdmin(request.admin());
        return toResponse(userRepository.save(user));
    }

    private void ensureEmailAvailable(String email, Long currentUserId) {
        userRepository.findByEmailIgnoreCase(email)
                .filter(existing -> currentUserId == null || !existing.getId().equals(currentUserId))
                .ifPresent(existing -> {
                    throw new BadRequestException("User with this email already exists", "USER_EMAIL_EXISTS");
                });
    }

    private void ensureAnAdminRemains(UserAccount user, boolean requestedAdmin) {
        if (user.isAdmin() && !requestedAdmin && userRepository.countByAdminTrue() <= 1) {
            throw new BadRequestException("At least one admin user is required", "USER_LAST_ADMIN");
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private UserResponse toResponse(UserAccount user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.isAdmin(),
                user.getCreatedAt());
    }
}
