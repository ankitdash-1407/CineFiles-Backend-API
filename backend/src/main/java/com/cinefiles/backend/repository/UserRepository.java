package com.cinefiles.backend.repository;

import com.cinefiles.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // Spring Boot writes the SQL to find a user by their username automatically
    User findByUsername(String username);
}
