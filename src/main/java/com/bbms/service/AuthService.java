package com.bbms.service;

import com.bbms.dao.UserDAO;
import com.bbms.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

/**
 * Handles user authentication and password management.
 * NOTE: In production this would delegate to university LDAP.
 *       For the academic project, credentials are stored locally with BCrypt hashing.
 */
public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    /**
     * Authenticates a user by ID and plain-text password.
     * Returns the User object on success, null on failure.
     */
    public User authenticate(String userId, String plainPassword) throws SQLException {
        User user = userDAO.getUserById(userId);
        if (user == null) {
            System.out.println("[DEBUG] Auth FAILED: User ID '" + userId + "' not found in database.");
            return null;
        }
        if (user.getStatus() != User.UserStatus.ACTIVE) {
            System.out.println("[DEBUG] Auth FAILED: User ID '" + userId + "' is not ACTIVE. Status: " + user.getStatus());
            return null;
        }
        if (user.getPasswordHash() == null) {
            System.out.println("[DEBUG] Auth FAILED: Password hash is null for User ID '" + userId + "'.");
            return null;
        }
        if (!BCrypt.checkpw(plainPassword, user.getPasswordHash())) {
            System.out.println("[DEBUG] Auth FAILED: Password mismatch for User ID '" + userId + "'.");
            return null;
        }
        System.out.println("[DEBUG] Auth SUCCESS: Logged in as " + userId + " (" + user.getUserType() + ")");
        return user;
    }

    /**
     * Hashes a plain-text password using BCrypt (10 rounds).
     */
    public String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
    }

    /**
     * Changes a user's password after verifying the old one.
     */
    public boolean changePassword(String userId, String oldPassword, String newPassword)
            throws SQLException {
        User user = authenticate(userId, oldPassword);
        if (user == null) return false;
        String newHash = hashPassword(newPassword);
        return userDAO.updatePassword(userId, newHash);
    }
}
