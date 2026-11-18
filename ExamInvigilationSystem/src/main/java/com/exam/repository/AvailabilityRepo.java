package com.exam.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.exam.entity.Availability;

public interface AvailabilityRepo extends JpaRepository<Availability, Long> {

    //FR-005: It shows all availability of one faculty
    List<Availability> findByFaculty_FacultyId(Long facultyId);
}