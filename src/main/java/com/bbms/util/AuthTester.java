package com.bbms.util;

import com.bbms.service.AuthService;
import com.bbms.model.User;
import java.sql.SQLException;

public class AuthTester {
    public static void main(String[] args) {
        AuthService authService = new AuthService();
        String testUserId = "ADMIN001";
        String testPassword = "Admin@123";

        System.out.println("=== BBMS Authentication Diagnostic Tool ===");
        System.out.println("Testing Login for: " + testUserId);
        System.out.println("Using Password: " + testPassword);

        try {
            User user = authService.authenticate(testUserId, testPassword);
            if (user != null) {
                System.out.println("SUCCESS: Authentication passed!");
                System.out.println("User Type: " + user.getUserType());
                System.out.println("Status:    " + user.getStatus());
            } else {
                System.out.println("FAILURE: Authentication failed. Please check your credentials or database.");
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Database connection error.");
            e.printStackTrace();
        }
    }
}
