package com.bbms.util;

import com.bbms.dao.UserDAO;
import com.bbms.model.User;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Diagnostic utility to test database connection and print user table contents.
 * Run this as a regular Java application (not on Tomcat).
 */
public class DBTester {
    public static void main(String[] args) {
        System.out.println("=== BBMS Database Diagnostic Tool ===");
        
        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("SUCCESS: Connected to database.");
            System.out.println("URL: " + conn.getMetaData().getURL());
            System.out.println("User: " + conn.getMetaData().getUserName());
            
            UserDAO userDAO = new UserDAO();
            List<User> users = userDAO.getAllUsers();
            
            System.out.println("\n--- Registered Users (" + users.size() + ") ---");
            if (users.isEmpty()) {
                System.out.println("WARNING: No users found in the database!");
                System.out.println("Please run 'bbms_database.sql' to initialize the data.");
            } else {
                for (User u : users) {
                    System.out.printf("ID: %-12s | Name: %-20s | Role: %-10s | Status: %-10s | Hash: %s%n",
                            u.getUserId(), 
                            u.getFirstName() + " " + u.getLastName(),
                            u.getUserType(),
                            u.getStatus(),
                            u.getPasswordHash());
                }
            }
            
        } catch (SQLException e) {
            System.err.println("CRITICAL FAILURE: Could not connect to database.");
            System.err.println("Error: " + e.getMessage());
            System.err.println("\nPlease check your 'database.properties' file.");
            e.printStackTrace();
        }
    }
}
