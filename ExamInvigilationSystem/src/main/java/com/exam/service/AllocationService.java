package com.exam.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.exam.entity.DutyAllocation;
import com.exam.entity.Faculty;
import com.exam.repository.DutyAllocationRepo;
import com.exam.repository.FacultyRepo;

@Service
public class AllocationService {

    @Autowired private DutyAllocationRepo dutyRepo;
    @Autowired private FacultyRepo facultyRepo;

    // For Duty Allocation along with Clash Checking
    public DutyAllocation allocateDuty(DutyAllocation duty) {
        Long facultyId = duty.getFaculty().getFacultyId();

        // Get exam date for clash checking
        java.time.LocalDate examDate = duty.getExam().getExamDate();
        String slot = duty.getSlot();

        // Clash check - duty exists on same date, same slot for same faculty
        boolean clash = dutyRepo.existsByFaculty_FacultyIdAndExam_ExamDateAndSlot(facultyId, examDate, slot);
        if (clash) {
            throw new RuntimeException("Faculty already booked for this date and slot! Clash found.");
        }

        // No clash, set time
        duty.setAllocationTime(LocalDateTime.now());

        // Save duty
        DutyAllocation savedDuty = dutyRepo.save(duty);

        // Increase duty count of faculty by 1
        Faculty faculty = duty.getFaculty();
        faculty.setTotalDuties(faculty.getTotalDuties() + 1);
        facultyRepo.save(faculty);

        return savedDuty;
    }

    // Show list of duties for Admin Dashboard
    public List<DutyAllocation> getAllDuties() {
        return dutyRepo.findAll();
    }

    // Show duty of one faculty for Faculty Dashboard
    public List<DutyAllocation> getDutiesByFaculty(Long facultyId) {
        return dutyRepo.findByFaculty_FacultyId(facultyId);
    }
}
