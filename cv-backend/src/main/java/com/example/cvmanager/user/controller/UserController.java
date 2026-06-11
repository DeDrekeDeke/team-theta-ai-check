package com.example.cvmanager.user.controller;

import com.example.cvmanager.auth.security.AuthenticatedUser;
import com.example.cvmanager.common.security.AdminAccessService;
import com.example.cvmanager.user.dto.UserCreateRequest;
import com.example.cvmanager.user.dto.UserResponse;
import com.example.cvmanager.user.dto.UserUpdateRequest;
import com.example.cvmanager.user.model.UserAccount;
import com.example.cvmanager.user.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final AdminAccessService adminAccessService;
    private final UserService userService;

    public UserController(AdminAccessService adminAccessService, UserService userService) {
        this.adminAccessService = adminAccessService;
        this.userService = userService;
    }

    /**
     * Lists all user accounts for an admin caller.
     *
     * @param authorizationHeader bearer token used to verify admin access
     * @return user summaries without password data
     */
    @GetMapping
    public List<UserResponse> listUsers(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        adminAccessService.requireAdmin(authorizationHeader);
        return userService.listUsers();
    }

    /**
     * Creates a regular user account from validated input.
     *
     * @param authorizationHeader bearer token used to verify admin access
     * @param request email, display name, and raw password for the new user
     * @return the created user summary without password data
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader,
            @Valid @RequestBody UserCreateRequest request) {
        adminAccessService.requireAdmin(user);
        return userService.createUser(request);
    }

    /**
     * Returns one user account by database id for an admin caller.
     *
     * @param authorizationHeader bearer token used to verify admin access
     * @param id user id from the request path
     * @return user summary without password data
     */
    @GetMapping("/{id}")
    public UserResponse getUser(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader,
            @PathVariable Long id) {
        adminAccessService.requireAdmin(authorizationHeader);
        return userService.getUser(id);
    }

    /**
     * Updates editable user account fields, including the admin flag and optional password.
     *
     * @param authorizationHeader bearer token used to verify admin access
     * @param id user id from the request path
     * @param request email, display name, optional raw password, and admin-role state to store
     * @return updated user summary without password data
     */
    @PutMapping("/{id}")
    public UserResponse updateUser(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader,
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        UserAccount currentAdmin = adminAccessService.requireAdmin(authorizationHeader);
        return userService.updateUser(id, request, currentAdmin.getId());
    }
}
