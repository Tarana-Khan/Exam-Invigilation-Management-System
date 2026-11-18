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

    @Autowired
    private DutyAllocationRepo dutyRepo;

    @Autowired
    private FacultyRepo facultyRepo;

    // FR-006 + FR-007: For Duty Allocation along with Clash Checking
    public DutyAllocation allocateDuty(DutyAllocation duty) {

        Long facultyId = duty.getFaculty().getFacultyId();

        // To fetch exam date for checking clash
        java.time.LocalDate examDate = duty.getExam().getExamDate();
        String slot = duty.getSlot();

        // CLASH CHECK: It check duty exist on same date, same slot of same faculty
        boolean clash = dutyRepo.existsByFaculty_FacultyIdAndExam_ExamDateAndSlot(facultyId, examDate, slot);

        if (clash) {
            throw new RuntimeException("Faculty already booked for this date and slot! Clash found.");
        }

        // clash not exist set time 
        duty.setAllocationTime(LocalDateTime.now());

        // saving duty
        DutyAllocation savedDuty = dutyRepo.save(duty);

        // FR-011: Increase duty count of faculty by 1
        Faculty faculty = duty.getFaculty();
        faculty.setTotalDuties(faculty.getTotalDuties() + 1);
        facultyRepo.save(faculty);

        return savedDuty;
    }

    // It shows list of duties for Admin Dashboard 
    public List<DutyAllocation> getAllDuties() {
        return dutyRepo.findAll();
    }

    // It shows duty of one faculty for Faculty Dashboard 
    public List<DutyAllocation> getDutiesByFaculty(Long facultyId) {
        return dutyRepo.findByFaculty_FacultyId(facultyId);
    }
}