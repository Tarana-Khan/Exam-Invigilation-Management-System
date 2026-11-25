package com.exam.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.exam.entity.Faculty;
import com.exam.repository.FacultyRepo;

@Controller
public class LoginController {

    @Autowired
    private FacultyRepo facultyRepo;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    // FR-001: Simple Login, username + password check
    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          Model model) {

        Faculty faculty = facultyRepo.findByUsername(username);

        if (faculty != null && faculty.getPassword().equals(password)) {

            // Redirect According to role
            if ("ADMIN".equalsIgnoreCase(faculty.getRole())) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/faculty/dashboard?facultyId=" + faculty.getFacultyId();
            }
        }

        model.addAttribute("error", "Wrong username or password!");
        return "login";
    }
}
