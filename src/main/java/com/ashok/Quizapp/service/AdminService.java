package com.ashok.Quizapp.service;

import org.springframework.stereotype.Service;

@Service
public class AdminService {
    private final String ADMIN_EMAIL = "admin@example.com";
    private final String ADMIN_PASSWORD = "admin123";
    private boolean loggedIn = false;

    public boolean login(String email, String password) {
        if (ADMIN_EMAIL.equals(email) && ADMIN_PASSWORD.equals(password)) {
            loggedIn = true;
            return true;
        }
        return false;
    }
    public boolean isLoggedIn() {
        return loggedIn;
    }
    public void logout() {
        loggedIn = false;
    }
}
