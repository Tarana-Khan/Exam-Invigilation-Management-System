package com.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.exam.entity.Faculty;

public interface FacultyRepo extends JpaRepository<Faculty, Long> {

    //FR-001 For Login, Searching faculty from username
    Faculty findByUsername(String username);
}