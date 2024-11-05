package com.ashok.Quizapp.dao;

import com.ashok.Quizapp.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionDao extends JpaRepository<Question, Integer> {
    @Query("SELECT q FROM Question q WHERE q.difficultylevel = :difficulty AND q.category = :category ORDER BY random()")
    List<Question> findRandomQuestions(@Param("difficulty") String difficulty, @Param("category") String category, Pageable pageable);
}
