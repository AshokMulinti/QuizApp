package com.ashok.Quizapp.controller.admin;

import com.ashok.Quizapp.model.Question;
import com.ashok.Quizapp.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @GetMapping("/questions")
    public String getAllQuestions(Model model) {
        List<Question> questions = questionService.getAllQuestions();
        model.addAttribute("questions", questions);
        return "admin/questions"; // Thymeleaf template for displaying questions
    }

    @GetMapping("/questions/add")
    public String addQuestionForm(Model model) {
        model.addAttribute("question", new Question());
        return "admin/addQuestion"; // Thymeleaf template for adding a question
    }

    @PostMapping("/questions/add")
    public String addQuestion(@ModelAttribute Question question) {
        questionService.addQuestion(question);
        return "redirect:/admin/questions"; // Redirect to questions list
    }

    @GetMapping("/questions/edit/{id}")
    public String editQuestionForm(@PathVariable Integer id, Model model) {
        Optional<Question> question = questionService.getQuestionById(id);
        if (question.isPresent()) {
            model.addAttribute("question", question.get());
            return "admin/editQuestion"; // Thymeleaf template for editing a question
        }
        return "redirect:/admin/questions"; // Redirect if question not found
    }

    @PostMapping("/questions/edit")
    public String editQuestion(@ModelAttribute Question question) {
        questionService.updateQuestion(question);
        return "redirect:/admin/questions"; // Redirect to questions list
    }

    @GetMapping("/questions/delete/{id}")
    public String deleteQuestion(@PathVariable Integer id) {
        questionService.deleteQuestionById(id);
        return "redirect:/admin/questions"; // Redirect to questions list
    }
}
