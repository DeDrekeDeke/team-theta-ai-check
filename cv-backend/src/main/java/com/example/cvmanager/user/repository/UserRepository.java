package com.example.cvmanager.user.repository;

import com.example.cvmanager.user.model.UserAccount;
import com.example.cvmanager.user.model.UserRole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByEmailIgnoreCase(String email);

    long countByRole(UserRole role);
}
