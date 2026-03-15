package com.github.birulazena.AuthenticationService.repository;

import com.github.birulazena.AuthenticationService.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    void deleteUserByUserId(Long id);

    boolean existsByUsername(String username);
}
