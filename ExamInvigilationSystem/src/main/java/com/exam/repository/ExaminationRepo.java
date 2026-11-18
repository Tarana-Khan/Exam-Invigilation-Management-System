package com.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.exam.entity.Examination;

public interface ExaminationRepo extends JpaRepository<Examination, Long> {
}