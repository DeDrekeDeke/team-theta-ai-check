package com.example.cvmanager.common.security;

import com.example.cvmanager.auth.security.AuthenticatedUser;
import com.example.cvmanager.common.exception.ForbiddenException;
import com.example.cvmanager.common.exception.UnauthorizedException;
import org.springframework.stereotype.Service;

@Service
public class AdminAccessService {

    public void requireAdmin(AuthenticatedUser user) {
        if (user == null) {
            throw new UnauthorizedException("Authentication is required", "AUTH_REQUIRED");
        }
        if (!user.admin()) {
            throw new ForbiddenException("Admin access is required", "ADMIN_REQUIRED");
        }
    }

    public void requireOwnerOrAdmin(AuthenticatedUser user, Long ownerUserId) {
        if (user == null) {
            throw new UnauthorizedException("Authentication is required", "AUTH_REQUIRED");
        }
        if (!user.admin() && !user.userId().equals(ownerUserId)) {
            throw new ForbiddenException("You do not have access to this resource", "ACCESS_DENIED");
        }
    }
}
