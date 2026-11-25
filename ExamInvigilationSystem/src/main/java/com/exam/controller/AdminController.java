package com.exam.controller;

import java.util.ArrayList;
import java.util.List;
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
        List<DutyAllocation> allDuties = allocationService.getAllDuties();
        List<Availability> allAvail = availabilityRepo.findAll();

        // Find duties where faculty is Unavailable after duty allocation
        List<DutyAllocation> conflictDuties = new ArrayList<>();
        for (DutyAllocation d : allDuties) {
            if (d.getFaculty() == null || d.getExam() == null) continue;
            for (Availability av : allAvail) {
                if (av.getFaculty() != null && av.getExam() != null
                    && av.getFaculty().getFacultyId().equals(d.getFaculty().getFacultyId())
                    && av.getExam().getExamId().equals(d.getExam().getExamId())
                    && "Unavailable".equalsIgnoreCase(av.getStatus())) {
                    conflictDuties.add(d);
                    break;
                }
            }
        }

        model.addAttribute("duties", allDuties);
        model.addAttribute("conflictDuties", conflictDuties);
        model.addAttribute("conflictCount", conflictDuties.size());
        // Only faculty, no admin in list
        model.addAttribute("faculties", facultyRepo.findByRole("FACULTY"));
        model.addAttribute("exams", examRepo.findAll());
        model.addAttribute("classrooms", classroomRepo.findAll());
        model.addAttribute("allAvailabilities", allAvail);
        model.addAttribute("newFaculty", new Faculty());
        model.addAttribute("newExam", new Examination());
        model.addAttribute("newClassroom", new Classroom());
        return "admin-dashboard";
    }

    // Add new Faculty, role is set to FACULTY automatically
    @PostMapping("/admin/add-faculty")
    public String addFaculty(@ModelAttribute Faculty faculty) {
        faculty.setRole("FACULTY");
        facultyRepo.save(faculty);
        return "redirect:/admin/dashboard";
    }

    // Add new Exam, skip if same date and same slot already exists
    @PostMapping("/admin/add-exam")
    public String addExam(@ModelAttribute Examination exam) {
        boolean exists = examRepo.findAll().stream()
            .anyMatch(e -> e.getExamDate() != null && e.getExamDate().equals(exam.getExamDate())
                        && e.getSlot() != null
                        && e.getSlot().equalsIgnoreCase(exam.getSlot()));
        if (!exists) {
            examRepo.save(exam);
        }
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
                             @RequestParam Long classroomId) {
        // Stop if faculty is Unavailable for this exam
        for (Availability av : availabilityRepo.findByFaculty_FacultyId(facultyId)) {
            if (av.getExam() != null
                && av.getExam().getExamId().equals(examId)
                && "Unavailable".equalsIgnoreCase(av.getStatus())) {
                return "redirect:/admin/dashboard?unavailable";
            }
        }
        try {
            Examination exam = examRepo.findById(examId).orElse(null);
            DutyAllocation duty = new DutyAllocation();
            duty.setFaculty(facultyRepo.findById(facultyId).orElse(null));
            duty.setExam(exam);
            duty.setClassroom(classroomRepo.findById(classroomId).orElse(null));
            // Slot = Exam ka slot, alag se nahi
            duty.setSlot(exam != null ? exam.getSlot() : "Morning");
            allocationService.allocateDuty(duty);
            return "redirect:/admin/dashboard?success";
        } catch (RuntimeException e) {
            return "redirect:/admin/dashboard?clash";
        }
    }
    // Delete duty - when faculty became Unavailable, admin can remove and re-allocate
    @PostMapping("/admin/delete-duty")
    public String deleteDuty(@RequestParam Long dutyId) {
        DutyAllocation d = dutyRepo.findById(dutyId).orElse(null);
        if (d != null) {
            Faculty f = d.getFaculty();
            if (f != null && f.getTotalDuties() > 0) {
                f.setTotalDuties(f.getTotalDuties() - 1);
                facultyRepo.save(f);
            }
            dutyRepo.deleteById(dutyId);
        }
        return "redirect:/admin/dashboard?deleted";
    }

    // Change duty to new faculty
    @PostMapping("/admin/change-duty")
    public String changeDuty(@RequestParam Long dutyId,
                             @RequestParam Long newFacultyId) {
        DutyAllocation d = dutyRepo.findById(dutyId).orElse(null);
        if (d == null || d.getExam() == null) {
            return "redirect:/admin/dashboard";
        }
        // Stop if new faculty is Unavailable
        for (Availability av : availabilityRepo.findByFaculty_FacultyId(newFacultyId)) {
            if (av.getExam() != null && av.getExam().getExamId().equals(d.getExam().getExamId())
                && "Unavailable".equalsIgnoreCase(av.getStatus())) {
                return "redirect:/admin/dashboard?unavailable";
            }
        }
        // Stop if new faculty already has duty on same date and slot
        boolean clash = dutyRepo.existsByFaculty_FacultyIdAndExam_ExamDateAndSlot(newFacultyId, d.getExam().getExamDate(), d.getSlot());
        if (clash) {
            return "redirect:/admin/dashboard?clash";
        }
        // Decrease old faculty count
        Faculty oldF = d.getFaculty();
        if (oldF != null && oldF.getTotalDuties() > 0) {
            oldF.setTotalDuties(oldF.getTotalDuties() - 1);
            facultyRepo.save(oldF);
        }
        // Set new faculty
        Faculty newF = facultyRepo.findById(newFacultyId).orElse(null);
        d.setFaculty(newF);
        dutyRepo.save(d);
        if (newF != null) {
            newF.setTotalDuties(newF.getTotalDuties() + 1);
            facultyRepo.save(newF);
        }
        return "redirect:/admin/dashboard?changed";
    }
}
