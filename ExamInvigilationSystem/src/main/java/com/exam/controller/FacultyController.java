package com.exam.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.exam.entity.Availability;
import com.exam.entity.Examination;
import com.exam.entity.Faculty;
import com.exam.repository.AvailabilityRepo;
import com.exam.repository.ExaminationRepo;
import com.exam.repository.FacultyRepo;
import com.exam.service.AllocationService;

@Controller
public class FacultyController {

    @Autowired
    private AllocationService allocationService;

    @Autowired
    private FacultyRepo facultyRepo;

    @Autowired
    private AvailabilityRepo availabilityRepo;

    @Autowired
    private ExaminationRepo examinationRepo;

    // FR-008 + FR-009: Shows faculty duty
    @GetMapping("/faculty/dashboard")
    public String facultyDashboard(@RequestParam Long facultyId, Model model) {
        model.addAttribute("faculty", facultyRepo.findById(facultyId).orElse(null));
        model.addAttribute("duties", allocationService.getDutiesByFaculty(facultyId));
        model.addAttribute("exams", examinationRepo.findAll());
        model.addAttribute("availabilities", availabilityRepo.findByFaculty_FacultyId(facultyId));
        return "faculty-dashboard";
    }

    // Availability Save - Faculty Marks his availability/unavailability
    @PostMapping("/faculty/availability")
    public String saveAvailability(@RequestParam Long facultyId,
                                   @RequestParam Long examId,
                                   @RequestParam String status) {
        Faculty faculty = facultyRepo.findById(facultyId).orElse(null);
        Examination exam = examinationRepo.findById(examId).orElse(null);

        Availability availability = new Availability();
        availability.setFaculty(faculty);
        availability.setExam(exam);
        availability.setStatus(status);
        availabilityRepo.save(availability);

        return "redirect:/faculty/dashboard?facultyId=" + facultyId;
    }
}