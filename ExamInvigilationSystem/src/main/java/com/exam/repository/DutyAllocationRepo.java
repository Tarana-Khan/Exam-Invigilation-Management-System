package com.exam.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.exam.entity.DutyAllocation;

public interface DutyAllocationRepo extends JpaRepository<DutyAllocation, Long> {

    //FR-007: Clash Prevention: It check duty exist on same date, same slot of same faculty
    boolean existsByFaculty_FacultyIdAndExam_ExamDateAndSlot(Long facultyId, LocalDate examDate, String slot);

    //FR-008: It shows all duty of one faculty
    List<DutyAllocation> findByFaculty_FacultyId(Long facultyId);
}