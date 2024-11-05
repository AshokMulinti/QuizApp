package com.ashok.Quizapp.dao;

import com.ashok.Quizapp.model.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizResultRepository extends JpaRepository<QuizResult, Integer> {
    List<QuizResult> findAllByOrderByPercentageDesc();
    Optional<QuizResult> findByEmail(String email);
    QuizResult findFirstByEmailOrderBySubmissionTimeDesc(String email);
}
