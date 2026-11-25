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

    @Autowired private AllocationService allocationService;
    @Autowired private FacultyRepo facultyRepo;
    @Autowired private AvailabilityRepo availabilityRepo;
    @Autowired private ExaminationRepo examinationRepo;

    // Shows faculty duty
    @GetMapping("/faculty/dashboard")
    public String facultyDashboard(@RequestParam Long facultyId, Model model) {
        Faculty faculty = facultyRepo.findById(facultyId).orElse(null);
        if (faculty == null) {
            return "redirect:/login";
        }
        model.addAttribute("faculty", faculty);
        model.addAttribute("duties", allocationService.getDutiesByFaculty(facultyId));
        model.addAttribute("exams", examinationRepo.findAll());
        model.addAttribute("availabilities", availabilityRepo.findByFaculty_FacultyId(facultyId));
        return "faculty-dashboard";
    }

    // Faculty marks availability - if duty already given and now Unavailable, admin will see alert
    @PostMapping("/faculty/availability")
    public String saveAvailability(@RequestParam Long facultyId,
                                   @RequestParam Long examId,
                                   @RequestParam String status) {
        Faculty faculty = facultyRepo.findById(facultyId).orElse(null);
        Examination exam = examinationRepo.findById(examId).orElse(null);
        if (faculty == null || exam == null) {
            return "redirect:/faculty/dashboard?facultyId=" + facultyId;
        }
        // If already marked for this exam, update same row, do not create duplicate
        Availability old = null;
        for (Availability a : availabilityRepo.findByFaculty_FacultyId(facultyId)) {
            if (a.getExam() != null && a.getExam().getExamId().equals(examId)) {
                old = a;
                break;
            }
        }
        if (old == null) {
            old = new Availability();
            old.setFaculty(faculty);
            old.setExam(exam);
        }
        old.setStatus(status);
        availabilityRepo.save(old);
        return "redirect:/faculty/dashboard?facultyId=" + facultyId + "&saved=true";
    }
}
