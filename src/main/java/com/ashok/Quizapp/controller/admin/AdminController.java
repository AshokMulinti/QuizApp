package com.ashok.Quizapp.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {

    private final String ADMIN_USERNAME = "admin";  // Hardcoded admin username
    private final String ADMIN_PASSWORD = "password"; // Hardcoded admin password

    @GetMapping("/admin/login")
    public String loginPage() {
        return "admin/login"; // Thymeleaf template for admin login page
    }

    @PostMapping("/admin/login")
    public String authenticate(@RequestParam String username, @RequestParam String password, Model model) {
        if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
            return "admin/dashboard"; // Redirect to admin dashboard
        }
        model.addAttribute("error", "Incorrect Credentials");
        return "admin/login"; // Return to login page with error
    }
}
