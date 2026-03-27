package com.bbms.dao;

import com.bbms.model.User;
import com.bbms.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User entities.
 */
public class UserDAO {

    /** Retrieves a user by their university ID. */
    public User getUserById(String userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    /** Returns all users. */
    public List<User> getAllUsers() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY last_name, first_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    /** Returns all users of a specific type. */
    public List<User> getUsersByType(User.UserType type) throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE user_type = ? ORDER BY last_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    /** Inserts a new user record. */
    public boolean addUser(User user) throws SQLException {
        String sql = "INSERT INTO users (user_id, first_name, last_name, email, phone, " +
                     "department, user_type, status, registration_date, password_hash) " +
                     "VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,  user.getUserId());
            ps.setString(2,  user.getFirstName());
            ps.setString(3,  user.getLastName());
            ps.setString(4,  user.getEmail());
            ps.setString(5,  user.getPhone());
            ps.setString(6,  user.getDepartment());
            ps.setString(7,  user.getUserType().name());
            ps.setString(8,  user.getStatus().name());
            ps.setDate(9,    Date.valueOf(user.getRegistrationDate()));
            ps.setString(10, user.getPasswordHash());
            return ps.executeUpdate() > 0;
        }
    }

    /** Updates an existing user record. */
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET first_name=?, last_name=?, email=?, phone=?, " +
                     "department=?, user_type=?, status=? WHERE user_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getDepartment());
            ps.setString(6, user.getUserType().name());
            ps.setString(7, user.getStatus().name());
            ps.setString(8, user.getUserId());
            return ps.executeUpdate() > 0;
        }
    }

    /** Deletes a user by ID. */
    public boolean deleteUser(String userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            return ps.executeUpdate() > 0;
        }
    }

    /** Updates only the password hash for a user. */
    public boolean updatePassword(String userId, String passwordHash) throws SQLException {
        String sql = "UPDATE users SET password_hash=? WHERE user_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, passwordHash);
            ps.setString(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    /** Returns total number of active users. */
    public int countActiveUsers() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE status = 'ACTIVE'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // ── Private helpers ─────────────────────────────────────────────────────

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getString("user_id"));
        u.setFirstName(rs.getString("first_name"));
        u.setLastName(rs.getString("last_name"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        u.setDepartment(rs.getString("department"));
        u.setUserType(User.UserType.valueOf(rs.getString("user_type")));
        u.setStatus(User.UserStatus.valueOf(rs.getString("status")));
        Date rd = rs.getDate("registration_date");
        if (rd != null) u.setRegistrationDate(rd.toLocalDate());
        u.setPasswordHash(rs.getString("password_hash"));
        return u;
    }
}
