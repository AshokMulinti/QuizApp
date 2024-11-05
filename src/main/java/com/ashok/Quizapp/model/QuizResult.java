package com.ashok.Quizapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "quiz_result")
public class QuizResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String email;
    private int totalQuestions;
    private int score;
    private LocalDateTime submissionTime;
    private Double percentage;
    // Custom constructor without 'id'
    public QuizResult(String email, int totalQuestions, int score, LocalDateTime submissionTime,double percentage) {
        this.email = email;
        this.totalQuestions = totalQuestions;
        this.score = score;
        this.submissionTime = submissionTime;
        this.percentage = percentage;
    }
    public String getFormattedSubmissionTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return this.submissionTime.format(formatter);
    }
}
