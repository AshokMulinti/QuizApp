package com.ashok.Quizapp.service.impl;

import com.ashok.Quizapp.dao.QuestionDao;
import com.ashok.Quizapp.model.Question;
import com.ashok.Quizapp.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionDao questionDao;

    @Autowired
    public QuestionServiceImpl(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    @Override
    public List<Question> getAllQuestions() {
        return questionDao.findAll();
    }

    @Override
    public void addQuestion(Question question) {
        questionDao.save(question);
    }

    @Override
    public Optional<Question> getQuestionById(Integer id) {
        return questionDao.findById(id);
    }

    @Override
    public void updateQuestion(Question question) {
        questionDao.save(question);
    }

    @Override
    public void deleteQuestionById(Integer id) {
        questionDao.deleteById(id);
    }
}
