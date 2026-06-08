package com.example.cvmanager.user.service;

import com.example.cvmanager.common.exception.NotFoundException;
import com.example.cvmanager.user.dto.UserResponse;
import com.example.cvmanager.user.model.UserAccount;
import com.example.cvmanager.user.repository.UserRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> listUsers() {
        return userRepository.findAll(Sort.by("id")).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        return userRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("User not found", "USER_NOT_FOUND"));
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
