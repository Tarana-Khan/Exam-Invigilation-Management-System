package com.exam.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.exam.entity.Availability;
import com.exam.entity.Classroom;
import com.exam.entity.DutyAllocation;
import com.exam.entity.Examination;
import com.exam.entity.Faculty;
import com.exam.repository.AvailabilityRepo;
import com.exam.repository.ClassroomRepo;
import com.exam.repository.DutyAllocationRepo;
import com.exam.repository.ExaminationRepo;
import com.exam.repository.FacultyRepo;
import com.exam.service.AllocationService;

@Controller
public class AdminController {

    @Autowired private FacultyRepo facultyRepo;
    @Autowired private ExaminationRepo examRepo;
    @Autowired private ClassroomRepo classroomRepo;
    @Autowired private DutyAllocationRepo dutyRepo;
    @Autowired private AllocationService allocationService;
    @Autowired private AvailabilityRepo availabilityRepo;

    // Show Admin Dashboard
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("duties", allocationService.getAllDuties());
        model.addAttribute("faculties", facultyRepo.findAll());
        model.addAttribute("exams", examRepo.findAll());
        model.addAttribute("classrooms", classroomRepo.findAll());
        // Show all faculty availability to admin
        model.addAttribute("allAvailabilities", availabilityRepo.findAll());
        model.addAttribute("newFaculty", new Faculty());
        model.addAttribute("newExam", new Examination());
        model.addAttribute("newClassroom", new Classroom());
        return "admin-dashboard";
    }

    // Add new Faculty
    @PostMapping("/admin/add-faculty")
    public String addFaculty(@ModelAttribute Faculty faculty) {
        facultyRepo.save(faculty);
        return "redirect:/admin/dashboard";
    }

    // Add new Exam
    @PostMapping("/admin/add-exam")
    public String addExam(@ModelAttribute Examination exam) {
        examRepo.save(exam);
        return "redirect:/admin/dashboard";
    }

    // Add new Classroom
    @PostMapping("/admin/add-classroom")
    public String addClassroom(@ModelAttribute Classroom classroom) {
        classroomRepo.save(classroom);
        return "redirect:/admin/dashboard";
    }

    // Allocate duty to faculty
    @PostMapping("/admin/allocate")
    public String assignDuty(@RequestParam Long facultyId,
                             @RequestParam Long examId,
                             @RequestParam Long classroomId,
                             @RequestParam String slot) {
        // Check if faculty is unavailable for this exam
        for (Availability av : availabilityRepo.findByFaculty_FacultyId(facultyId)) {
            if (av.getExam() != null
                && av.getExam().getExamId().equals(examId)
                && "Unavailable".equalsIgnoreCase(av.getStatus())) {
                // Stop here, do not give duty
                return "redirect:/admin/dashboard?unavailable";
            }
        }

        try {
            DutyAllocation duty = new DutyAllocation();
            duty.setFaculty(facultyRepo.findById(facultyId).orElse(null));
            duty.setExam(examRepo.findById(examId).orElse(null));
            duty.setClassroom(classroomRepo.findById(classroomId).orElse(null));
            duty.setSlot(slot);

            // Save duty, service will check clash
            allocationService.allocateDuty(duty);
            return "redirect:/admin/dashboard?success";

        } catch (RuntimeException e) {
            // Faculty already has duty on same date and slot
            return "redirect:/admin/dashboard?clash";
        }
    }
}