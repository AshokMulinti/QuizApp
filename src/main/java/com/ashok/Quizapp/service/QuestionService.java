package com.ashok.Quizapp.service;

import com.ashok.Quizapp.model.Question;
import java.util.List;
import java.util.Optional;

public interface QuestionService {
    List<Question> getAllQuestions();
    void addQuestion(Question question);
    Optional<Question> getQuestionById(Integer id);
    void updateQuestion(Question question);
    void deleteQuestionById(Integer id);
}
