package com.exam.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.exam.entity.Faculty;

public interface FacultyRepo extends JpaRepository<Faculty, Long> {

    // For Login, searching faculty from username
    Faculty findByUsername(String username);

    // Get only faculty or only admin by role
    List<Faculty> findByRole(String role);
}
