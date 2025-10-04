package com.example.taskmanager.repository;

import com.example.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Use EntityGraph to eagerly fetch user data
    @EntityGraph(attributePaths = {"user"})
    List<Task> findByUserId(Long userId);

    // Use EntityGraph to eagerly fetch user data for all tasks
    @EntityGraph(attributePaths = {"user"})
    List<Task> findAllByOrderByCreatedAtDesc();

    long countByCompleted(boolean completed);
}