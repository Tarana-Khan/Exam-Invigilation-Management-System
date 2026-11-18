package com.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.exam.entity.Classroom;

public interface ClassroomRepo extends JpaRepository<Classroom, Long> {
}