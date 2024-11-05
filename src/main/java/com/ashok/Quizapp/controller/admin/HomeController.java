package com.ashok.Quizapp.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/quizApp")
    public String home() {
        return "home"; // Thymeleaf template for home page
    }
}
