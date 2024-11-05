package com.ashok.Quizapp.service;
import com.ashok.Quizapp.dao.QuestionDao;
import com.ashok.Quizapp.dao.QuizResultRepository;
import com.ashok.Quizapp.dao.UserRepository;
import com.ashok.Quizapp.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    QuestionDao questionDao;
    @Autowired
    QuizResultRepository quizResultRepository;
     public boolean isEmailExists(String email) {
        return userRepository.findByEmail(email) != null; // Adjust according to your repository method
    }

    public void registerUser(User user) {
        userRepository.save(user); // Save user to the database
    }

    public User loginUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user; // Authenticate user
        }
        return null; // Invalid login
    }
    public List<Question> getQuestions(String difficulty, String category, int limit) {
        return questionDao.findRandomQuestions(difficulty, category, PageRequest.of(0, limit));
    }
    public List<QuizResult> getTopUsersByPercentage() {
        return quizResultRepository.findAllByOrderByPercentageDesc();
    }
}
