package com.example.bysell.repositories;

import com.example.bysell.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);
    Optional<User> findById(Long id);
}
