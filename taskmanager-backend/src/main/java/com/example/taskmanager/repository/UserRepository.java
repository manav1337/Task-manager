package com.example.taskmanager.repository;

import com.example.taskmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Use derived method instead of custom SQL
    Optional<User> findByUsername(String username);

    // Use derived method instead of custom SQL
    Optional<User> findByEmail(String email);

    // Use derived method - Spring will generate the SQL automatically
    Boolean existsByUsername(String username);

    // Use derived method - Spring will generate the SQL automatically
    Boolean existsByEmail(String email);
}