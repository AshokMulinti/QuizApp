package com.ashok.Quizapp.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController {

    @RequestMapping("/custom-error") // Use a different path
    public String handleCustomError() {
        return "customError"; // Ensure this corresponds to the correct Thymeleaf template
    }
}
