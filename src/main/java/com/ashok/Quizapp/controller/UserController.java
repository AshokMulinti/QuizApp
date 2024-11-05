package com.ashok.Quizapp.controller;

import com.ashok.Quizapp.dao.QuizResultRepository;
import com.ashok.Quizapp.model.Question;
import com.ashok.Quizapp.service.QuestionService;
import com.ashok.Quizapp.model.QuizResult;
import com.ashok.Quizapp.model.User;
import com.ashok.Quizapp.service.UserService;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.ByteArrayOutputStream;
import java.lang.String;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuizResultRepository quizResultRepository;

    /**
     * Displays the user home page with an empty User object.
     */
    @GetMapping
    public String userHome(Model model) {
        model.addAttribute("user", new User());
        return "user/userHome";
    }

    /**
     * Registers a new user if the email does not already exist.
     */
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        if (userService.isEmailExists(user.getEmail())) {
            model.addAttribute("error", "Email already exists. Please use a different email.");
            return "user/userHome";
        }

        userService.registerUser(user);
        model.addAttribute("registrationSuccess", true);
        return "user/userHome";
    }

    /**
     * Logs in the user based on email and password.
     */
    @PostMapping("/login")
    public String loginUser(@RequestParam("email") String email,
                            @RequestParam("password") String password,
                            Model model) {
        User user = userService.loginUser(email, password);
        if (user != null) {
            model.addAttribute("loginSuccess", true);
            return "user/dashboard"; // Redirect to dashboard on successful login
        } else {
            model.addAttribute("error", "Incorrect email or password."); // Specific error message
            model.addAttribute("user", new User()); // Add an empty User object to the model
            return "user/userHome"; // Return to the user home view
        }
    }

    /**
     * Displays the form to create a new quiz.
     */
    @GetMapping("/createQuizz")
    public String createQuizzForm() {
        return "user/createQuizz";
    }

    /**
     * Starts the quiz based on selected parameters.
     */
    @GetMapping("/quiz/start")
    public String startQuiz(@RequestParam("questions") int questions,
                            @RequestParam("difficulty") String difficulty,
                            @RequestParam("category") String category,
                            Model model) {
        List<Question> questionList = userService.getQuestions(difficulty, category, questions);
        model.addAttribute("questions", questionList);
        return "user/quizPage";
    }

    /**
     * Submits the quiz and calculates the score.
     */
    @PostMapping("/submitQuiz")
    public String submitQuiz(@RequestParam(required = false) String email,
                             @RequestParam Map<String, String> answers,
                             Model model) {
        int score = 0;
        int totalQuestions = 0;

        // Calculate score
        for (Map.Entry<String, String> entry : answers.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("answers[")) {
                try {
                    int questionId = Integer.parseInt(key.replace("answers[", "").replace("]", ""));
                    Optional<Question> optionalQuestion = questionService.getQuestionById(questionId);

                    if (optionalQuestion.isPresent() && entry.getValue().equals(optionalQuestion.get().getRightAnswer())) {
                        score++;
                    }
                    totalQuestions++;
                } catch (NumberFormatException e) {
                    System.err.println("Failed to parse question ID: " + key);
                }
            }
        }

        LocalDateTime submissionTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedSubmissionTime = submissionTime.format(formatter);

        // Calculate percentage
        double percentage = totalQuestions > 0 ? ((double) score / totalQuestions) * 100 : 0.0;

        // Save or update result in the database
        if (email != null && !email.isEmpty()) {
            Optional<QuizResult> existingResult = quizResultRepository.findByEmail(email);

            if (existingResult.isPresent()) {
                // Update the existing result
                QuizResult quizResult = existingResult.get();
                quizResult.setScore(score);
                quizResult.setTotalQuestions(totalQuestions);
                quizResult.setSubmissionTime(submissionTime);
                quizResultRepository.save(quizResult);
            } else {
                // Save a new result
                QuizResult quizResult = new QuizResult(email, totalQuestions, score, submissionTime, percentage);
                quizResultRepository.save(quizResult);
            }
        }

        // Add attributes to the model for the result page
        model.addAttribute("score", score);
        model.addAttribute("totalQuestions", totalQuestions);
        model.addAttribute("submissionTime", formattedSubmissionTime);
        model.addAttribute("percentage", String.format("%.2f", percentage)); // Format to 2 decimal places

        return "user/result"; // Redirect to the result page
    }

    @GetMapping("/toppers")
    public String viewToppers(Model model) {
        List<QuizResult> toppers = userService.getTopUsersByPercentage();
        model.addAttribute("toppers", toppers);
        return "user/toppers"; // Thymeleaf template
    }

    @GetMapping("/preparation")
    public String showPreparation() {
        return "user/preparation"; // Return the view name for your preparation page
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadCertificate(@RequestParam String email) {
        // Retrieve the quiz result based on the provided email
        Optional<QuizResult> resultOptional = quizResultRepository.findByEmail(email);

        // Check if the result exists
        if (resultOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Quiz result not found for the provided email.".getBytes());
        }

        QuizResult result = resultOptional.get();

        // Prepare the PDF document
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Add title
            addTitle(document, "Certificate of Completion");

            // Add congratulatory message
            addCongratulatoryMessage(document, result.getEmail());

            // Add additional message
            addMessage(document, "Thank you for participating in the quiz.");

            // Create and populate the table for quiz details
            Table table = createQuizDetailsTable(result);
            document.add(table);

            // Close the document
            document.close();
            byte[] pdfBytes = outputStream.toByteArray();

            // Prepare response headers
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=certificate.pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            // Log the exception (consider using a logging framework)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while generating the certificate.".getBytes());
        }
    }

    // Helper method to add title to the document
    private void addTitle(Document document, String titleText) {
        Paragraph title = new Paragraph(titleText)
                .setFontSize(24)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(new DeviceRgb(0, 102, 204)); // Dark blue color
        document.add(title);
        document.add(new Paragraph("\n")); // Add spacing
    }

    // Helper method to add a congratulatory message
    private void addCongratulatoryMessage(Document document, String email) {
        Paragraph message = new Paragraph("Congratulations, " + email + "!")
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(new DeviceRgb(255, 165, 0)); // Orange color
        document.add(message);
    }

    // Helper method to add a message
    private void addMessage(Document document, String messageText) {
        document.add(new Paragraph(messageText)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(new DeviceRgb(0, 0, 0))); // Black color
        document.add(new Paragraph("\n")); // Add spacing
    }

    // Helper method to create the quiz details table
    private Table createQuizDetailsTable(QuizResult result) {
        Table table = new Table(2);
        table.setWidth(UnitValue.createPercentValue(100)); // Set table width to 100%

        // Add header cells with styles
        table.addCell(createHeaderCell("Email", result.getEmail()));
        table.addCell(createHeaderCell("Total Questions", String.valueOf(result.getTotalQuestions())));
        table.addCell(createHeaderCell("Score", String.valueOf(result.getScore())));
        table.addCell(createHeaderCell("Submission Time", result.getFormattedSubmissionTime()));
        table.addCell(createHeaderCell("Percentage", result.getPercentage() + "%"));

        return table;
    }

    // Helper method to create a styled header cell
    private Cell createHeaderCell(String headerText, String cellText) {
        Cell headerCell = new Cell().add(new Paragraph(headerText))
                .setBold().setFontSize(14).setTextAlignment(TextAlignment.CENTER)
                .setBackgroundColor(new DeviceRgb(0, 204, 204)) // Light blue background
                .setBorder(new SolidBorder(new DeviceRgb(0, 0, 0), 2)); // Black border;

        Cell contentCell = new Cell().add(new Paragraph(cellText))
                .setBold().setFontSize(14).setTextAlignment(TextAlignment.CENTER)
                .setBorder(new SolidBorder(new DeviceRgb(0, 0, 0), 2)); // Black border

        return headerCell.add(contentCell);
    }
}